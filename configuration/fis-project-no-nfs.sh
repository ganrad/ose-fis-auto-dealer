#!/bin/bash
#Variables
hostname=`hostname`
user=`whoami`
ocpuser=`oc whoami`

echo "$user"

echo "Make new nfs directory..."
sudo mkdir /opt/nfs


echo "Update exports file ..."
sudo chown -R $user:$user /etc/exports
sudo chmod -R 777 /etc/exports
sudo echo '/opt/nfs *(rw,all_squash)' >> /etc/exports

#echo "Update fstab file ..."
#sudo chown -R $user:$user /etc/fstab
#sudo chmod -R 777 /etc/fstab
#sudo echo '/dev/sdb /opt/nfs xfs defaults  0 0' >> /etc/fstab

echo "Update permissions ..."
sudo chown -R $user:$user /opt/nfs
sudo chmod -R 777 /opt/nfs
sudo setsebool -P virt_use_nfs 1
sudo setsebool -P virt_sandbox_use_nfs 1

echo "Activate and add NFS to startup ..."
sudo systemctl enable rpcbind nfs-server
sudo systemctl start rpcbind nfs-server nfs-lock
sudo systemctl start nfs-idmap
sudo showmount -e

#echo "Add $ocpuser user to OSE admin..."
oadm policy add-cluster-role-to-user cluster-admin $user
oadm policy add-cluster-role-to-user admin $user


echo What is the name of your project, i.e. fis-apps?
read project

echo "Creating new project $project"

oc new-project $project

echo "Get test files from GIT ..."
curl https://raw.githubusercontent.com/tgaillard1/ose-fis-auto-dealer/master/data/vn01.xml > /opt/nfs/vn01.xml
curl https://raw.githubusercontent.com/tgaillard1/ose-fis-auto-dealer/master/data/vn02.xml > /opt/nfs/vn02.xml
curl https://raw.githubusercontent.com/tgaillard1/ose-fis-auto-dealer/master/data/vn03.xml > /opt/nfs/vn03.xml
curl https://raw.githubusercontent.com/tgaillard1/ose-fis-auto-dealer/master/data/vn04.xml > /opt/nfs/vn04.xml
curl https://raw.githubusercontent.com/tgaillard1/ose-fis-auto-dealer/master/configuration/fis-image-streams.json > fis-image-streams.json
curl https://raw.githubusercontent.com/tgaillard1/ose-fis-auto-dealer/master/configuration/mongodb-ephemeral-template.json > mongodb-ephemeral-template.json

# Create templates ..."
oc create -n openshift -f fis-image-streams.json
oc create -n $project -f fis-image-streams.json
oc create -n openshift -f mongodb-ephemeral-template.json
oc create -n $project -f mongodb-ephemeral-template.json

chmod -R 777 /opt/nfs

echo "Creating Mongo DB in project $project"
oc new-app -p MONGODB_USER=oseUser,MONGODB_PASSWORD=openshift,MONGODB_DATABASE=test,MONGODB_ADMIN_PASSWORD=admin123 mongodb-ephemeral

echo "Getting and creating secrets file ..."
curl https://raw.githubusercontent.com/tgaillard1/ose-fis-auto-dealer/master/configuration/secrets.yaml > secrets.yaml
oc create -f secrets.yaml
oc secrets add serviceaccount/default secret/fis-auto-db-secret --for=mount

echo "Get and create project template from Git"
curl https://raw.githubusercontent.com/tgaillard1/ose-fis-auto-dealer/master/kube-template.json > kube-template.json
oc create -f kube-template.json
echo "Validate temmpate created --> `oc get templates`"

echo "Get and create persistent volume for nfs ..."
curl https://raw.githubusercontent.com/tgaillard1/ose-fis-auto-dealer/master/configuration/pv.yaml > pv.yaml
sed -i "s/data-store.example.com/$hostname/g" pv.yaml
oc create -f pv.yaml

echo "Get and create persistent volume claim on nfs location ..."
curl https://raw.githubusercontent.com/tgaillard1/ose-fis-auto-dealer/master/configuration/pvc.yaml > pvc.yaml
oc create -f pvc.yaml
echo "Validate volume claim `oc get pvc`"

echo "Your project $project is now being created in OpenShift"
oc new-app --template=fis-auto-template --param=GIT_REPO=https://github.com/tgaillard1/ose-fis-auto-dealer.git

sleep 10
oc logs -f auto-dealer-app-1-build

#oc delete all -all -n fis-apps