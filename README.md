# OpenShift FIS microservice *ose-fis-auto-dealer*:

**Important Note:** This project assumes the readers have a basic working knowledge of *Red Hat OpenShift Enterprise v3.1/v3.2* (or upstream project -> OpenShift Origin) & are familiar with the underlying framework components such as Docker & Kubernetes.  Readers are also advised to familiarize themselves with the *Kubernetes* API object model (high level) before beginning to work on this *microservice* implementation.  For quick reference, links to a couple of useful on-line resources are listed below.

1.  [OpenShift Enterprise Documentation](https://docs.openshift.com/)
2.  [Kubernetes Documentation](http://kubernetes.io/docs/user-guide/pods/)

This project uses OpenShift FIS (Fuse Integration Services) tools and explains how to develop, build and deploy Apache Camel based microservices in OpenShift Enterprise v3.1/v3.2.

For building Apache Camel applications within Docker containers and then deploying the resulting container images onto OpenShift, developers can take two different approaches or paths.  The steps outlined here use approach # 1 (see below) in order to build and deploy this microservice application.

1.  S2I (Source to Image) workflow : Using this path, a user generates a template object definition (TOD) using the fabric8 Maven plug-in which is included in the OpenShift FIS tools package.  The TOD contains a list of kubernetes objects and also includes info. on the S2I image (builder image) which will be used to build the container image containing the camel application binaries along with the respective run-time (Fuse or Camel).  To learn more about FIS for OpenShift or types of runtimes an Apache Camel application can be deployed to, refer to this [blog] (http://blog.christianposta.com/cloud-native-camel-riding-with-jboss-fuse-and-openshift/) 
2.  Apache Maven Workflow : Using this path, the developer uses fabric8 Maven plug-in(s) to build the Apache Camel application, generate the docker image containing both the compiled application binary & the run-time, push the docker image to the registry & lastly generate the TOD containing the list of kubernetes objects necessary to deploy the application to OpenShift.  For more detailed info. on this workflow & steps for deploying a sample application using this workflow, please refer to this GitHub project <https://github.com/RedHatWorkshops/rider-auto-openshift>

## Description/Synopsis
This microservice exposes a RESTFul API with two *http* end-points.  The microservice has been adapted from it's original version posted 
by Wei Meilin - [jboss-fis-autodealer](https://github.com/jbossdemocentral/jboss-fis-autodealer).

The auto-dealer microservice application is implemented using Apache Camel routes (integration flows).  At a high level, the Camel routes execute the following sequence of steps (see description and diagram below) :

1.  Retrieve vehicle data (*'xxx.xml'*) files from a source directory.  This directory will be mounted on a NFS share/directory.
2.  Un-marshall/De-serialize the XML data read from files into JSON strings.
3.  Store the vehicle info (JSON data) within collections in MongoDB NoSQL persistent database.  
  **Note:** This example uses a OpenShift provided MongoDB *Instant App* template to demonstrate how to save/retrieve application data in a *'ephemeral'* ('non-persistent') database instance.  For real-world (production) applications, you will need to use the provided *'persistent'* MongoDB *Instant App* template.  While configuring this template, you will need to provide *Persistent Volume Claim* details so that the database is backed by a persistent storage volume. 
4.  Expose two REST (HTTP) end-points to allow users to query and retrieve (GET) vehicle information from the backend persistent data store (MongoDB).

![alt tag](https://raw.githubusercontent.com/ganrad/ose-fis-auto-dealer/master/ose-fis.001.png)

In order to retrieve vehicle information from a persistent store such as a file system (Step 1), we will need to mount a NFS (Linux Network File System) share into the OpenShift Pod running our microservice application. This can be done by defining a [Persistent Volume (PV)](https://docs.openshift.com/enterprise/3.1/architecture/additional_concepts/storage.html#persistent-volumes) object in OpenShift that points to the exported NFS directory.  Additionally, we will also need to create a [Persistent Volume Claim (PVC)](https://docs.openshift.com/enterprise/3.1/architecture/additional_concepts/storage.html#persistent-volume-claims) object in OpenShift and then specify the PVC information in the *'volume mount'* section within the Pod manifest.  This would essentially allow the application Pod to retrieve data from the mounted file system directory.

OpenShift comes with a wide variety of persistant storage plug-ins that allow containerized applications to retrieve/store data from multiple persistent storage systems.  The persistent storage plug-ins that ship with OpenShift can be viewed [here](https://docs.openshift.com/enterprise/3.1/install_config/persistent_storage/index.html).

Microservices are stateless and are ideal candidates for deploying onto a container application platform such as Red Hat OpenShift Enterprise.  Container images are essentially immutable and so data stored inside a running container is only available as long as the container is alive.  Once the container terminates (or is deleted/evicted), it's data is no longer available.  For this reason, we will be persisting the vehicle data read from the file system (NFS directory) into a persistent NoSQL database (MongoDB).  All REST API (HTTP) requests will be translated into queries by FIS routes and data will be fetched from the underlying MongoDB persistent database.

## Steps for deploying MongoDB container and FIS microservices on OpenShift Enterprise v3.1/v3.2
The steps listed below for building and deploying the microservice applications follows approach (1) described above, the S2I workflow.

### A] Deploy MongoDB NoSQL Database  

1.  Login to OpenShift using the Web UI - 

  ```
  https://<Host name or IP address of OSE master node>:8443/
  ```

  Or Alternatively use the command line interface (CLI) to create the project as shown below.

  ```
  $ oc login -u user -p password  
  ```
  
2.  Create a new project.  Use command shown below if using the CLI.

  ```
  $ oc new-project fis-apps
  ```

3.  Add a new application and name it 'mongodb'.  In the next screen, type 'mongodb' in the search text field and select the 'mongodb-ephemeral' database template.  Click next.  Specify values for MongoDB user name, password & database name as shown in the screenshot below.  Please note down these values as we will need them while creating *'secrets'* discussed in Step B below.  You can choose any value for the MongDB admin password.  Finally click on 'create' application.  See screenshots below.

  ![alt tag](https://raw.githubusercontent.com/ganrad/ose-fis-auto-dealer/master/mongodb-1.png)  
  
  ![alt tag](https://raw.githubusercontent.com/ganrad/ose-fis-auto-dealer/master/mongodb-2.png)

4.  Switch to the 'overview' tab on the left navigational panel & check to make sure the MongoDB application (Pod) has started ok.  See screenshot below.

  ![alt tag](https://raw.githubusercontent.com/ganrad/ose-fis-auto-dealer/master/mongodb-3.png)

### B] Create a *Secret* and update *Service Account* in OpenShift
A *secret* is used to hold sensitive information such as OAuth tokens, passwords and SSH keys in OpenShift.  Putting confidential and sensitive information such as database user names and passwords in a **secret** is much more safer and secure than storing them as plain text values directly in a application configuration file or in a Pod definition.  Using *secret* objects in OpenShift allows for more control over how confidential info. is stored and accessed and reduces the risk of accidental exposure.

We will be encrypting and storing the MongoDB user name, password and database name in a *secret* using the steps outlined below.

1.  Use the *curl* command to download & save the *secrets.yaml* file from the *configuration* directory.  If you specified the same values for MongoDB user name, password and database name as depicted in the picture in Step A:3 above, then you can skip Step 2 below.

2.  Generate the Base64 encoded value for the MongoDB *user name* using the command below.
  
  ```
  $ echo "mongodb.user=oseUser" | Base64 -w 0
  ```
  * Substitute the MongoDB **user name** you used in Step A:3 (above) in place of **oseUser** in the command above.
  * Copy the generated *Base64* (output of command above) value and save it in the *'secrets.yaml'* file which you downloaded in Step 1.  Values within the *data* stanza in the secrets file (YAML) take the form *'name: value'*.  See below.
  ```
  db.username: bW9uZ29kYi51c2VyPW9zZVVzZXIK
  ```
  * Repeat this command to generate Base64 encoded values for *mongodb.password* and *mongodb.database* & then save them in the *'secrets.yaml'* file.  Your *secrets.yaml* file should look like the definition below.  Encrypted values for the  secret data may differ based on the values your provided.
  
  ```
  apiVersion: v1
  kind: Secret
  metadata:
    name: fis-auto-db-secret
  type: Opaque
  data:
    db.name: bW9uZ29kYi5kYXRhYmFzZT10ZXN0Cg==
    db.username: bW9uZ29kYi51c2VyPW9zZVVzZXIK
    db.password: bW9uZ29kYi5wYXNzd29yZD1vcGVuc2hpZnQK
  ```

3. Use the command below to create the *secret* object.

  ```
  oc create -f secrets.yaml
  ```
  * List the *secret* objects
  ```
  oc get secrets
  ```

4.  Finally, add the newly created *secret* to the *default* Service Account.  In OpenShift, *Service Accounts* are used by system level components to authenticate against the Kubernetes API server.  Service Accounts store API token info. and allow system level components to access the API server, perform CRUD operations on API objects etc.  In addition to providing API credentials, an application Pod's service account determines which secrets the Pod is allowed to access and use.  Unless otherwise a Pod's definition specifies a particular service account, every Pod in a given project runs with the *'default'* service account.  So we will now add the *secret* we created in Step 3 to the *default* service account in our project.
  
  ```
  oc secrets add serviceaccount/default secret/fis-auto-db-secret --for=mount
  ```
  
### C] Deploy *ose-fis-auto-dealer* microservice

1.  Fork this repository so that it gets added to your GitHub account.
2.  Download the template file (template object definition) into your master node.
  * Click on *kube-template.json*, then click on *Raw*.  Copy the http URL and use the CURL command to download the template file to your OpenShift master node (OR to the server where you have installed OpenShift client tools).
  
  ```
  $ curl https://raw.githubusercontent.com/<your GIT account name>/ose-fis-auto-dealer/master/kube-template.json > kube-template.json
  ```
3.  Import the template file into your project 
  * Alternatively, you can import the template to the 'openshift' project using '-n openshift' option.  This would give all OpenShift users access to this template definition.
  
  ```
  $ oc create -f kube-template.json
  ```
  * To view all application templates in your current project
  ```
  $ oc get templates
  ```
4.  On each OpenShift node, enable writing to NFS volumes with SELinux. The -P option ensures the setting is persisted between reboots.

  ```
  $ setsebool -P virt_use_nfs 1
  $ setsebool -P virt_sandbox_use_nfs 1
  ```
5.  Create a *Persistent Volume* definition and save it in a file (as below).  Alternatively, use the *curl* command to download the *pv.yaml* file from the *configuration* directory.

  ```
  apiVersion: v1
  kind: PersistentVolume
  metadata: 
    name: pv001
  spec:
    capacity:
      storage: 250Mi
    accessModes:
    - ReadWriteOnce
    nfs:
      path: /opt/nfs
      server: data-store.example.com
      persistentVolumeReclaimPolicy: Recycle
  ```
  
  **path** : Directory (full path) exported by the NFS server.  
  **server** : Hostname (or IP address) of the NFS server.

6.  Create a *Persistent Volume Claim* definition and save it in a file (as below).  Alternatively, use the *curl* command to download the *pvc.yaml* file from the *configuration* directory.

   ```
   apiVersion: v1
   kind: PersistentVolumeClaim
   metadata:
     name: nfs-claim1
   spec:
     accessModes:
     - ReadWriteOnce
     resources:
       requests:
         storage: 225Mi
   ```
7.  Check if the PVC has been associated with the PV.

   ```
   [gradhakr@ose31-master ~]$ oc get pvc
   NAME         LABELS    STATUS    VOLUME    CAPACITY   ACCESSMODES   AGE
   nfs-claim1   <none>    Bound     pv001     250Mi      RWO           3h
   ```
8.  Create the microservice application
  * This command kicks off the S2I build process in OpenShift.
  * Alternatively, you can use the OpenShift Web UI to create the application.
  * Remember to substitute your GIT account user name in the GIT http url below.
  ```
  $ oc new-app --template=fis-auto-template --param=GIT_REPO=https://github.com/<your GIT account username>/ose-fis-auto-dealer.git
  ```
9.  Use the commands below to check the status of the build and deployment 
  * The build (Maven) might take a while (approx. 10-20 mins) to download all dependencies, build the code and then push the image into the integrated Docker registry.
  * Once the build completes and the image is pushed into the registry, the deployment process would start.
  * Check the builds
  ```
  $ oc get builds
  ```
  * Stream/view the build logs
  ```
  $ oc logs -f <build name | build pod name>
  ```
  * Check the deployment
  ```
  $ oc get dc
  ```
  * Check the pods.  After the deployment pod completes, the application pod should show a status of *running*.
  ```
  $ oc get pods
  ```
  * At this point, you should have successfully built an Apache Camel based RESTful microservice using OpenShift FIS tooling and deployed the same to OpenShift PaaS!
  
  ![alt tag](https://raw.githubusercontent.com/ganrad/ose-fis-auto-dealer/master/ose-auto-fis.png)
10.  Open a command line window and tail the output from the application Pod.
   
   ```
   $ oc get pods
   $ oc log pod -f <pod name>
   ```
   Substitute the name of your Pod in the command above.
11.  Create and save a few XML data files into the corresponding source directory (exported directory) on the NFS server.  Sample XML data files are provided in the *data* directory.  The XML files should be immediately read by this microservice, the data should be converted to JSON format & persisted to the collection *'ose'* within MongoDB database *'test'*.  You should also be able to view corresponding log messages in the command window as shown below.

   ```
   2016-05-17 22:52:08,531 [e://target/data] INFO  readVehicleFiles               - Read Vehicle Data File : /deployments/target/data/vn01.xml
   <?xml version="1.0"?>
   <vehicle>
	      <vehicleId>001</vehicleId>
	      <make>Honda</make>
	      <model>Civic</model>
	      <type>LX</type>
	      <year>2016</year>
	      <price>18999</price>
	      <inventoryCount>2</inventoryCount>
   </vehicle>
   ```
12.  Test the REST end-points using your browser. Substitute the correct values for route name, project name and 
openshift domain name as they apply to your OpenShift environment.
  * Test *'getVehicle'* end-point using URL below. The result of the REST API call should be JSON data. Vehicle numbers/IDs 
  which you can retrieve are vno01 ... vno04.  Substitute the exact vehicle ID you want to retrieve in the URL (below).
  ```
  http://route name-project name.openshift domain name/AutoDMS/getVehicle/001
  ```
  * Test *'availableVehicle'* end-point using URL below.
  ```
  http://route name-project name.openshift domain name/AutoDMS/availableVehicle/pricerange/20000/30000
  ```
  * See browser screenshot below.
  
  ![alt tag](https://raw.githubusercontent.com/ganrad/ose-fis-auto-dealer/ver3/results01.png)

13.  You can view the REST API responses in the Pod output / command window as shown below.

  ```
  2016-05-17 22:53:24,788 [tp1244815033-20] INFO  getVehicle                     - {
  "vehicleId" : "001",
  "make" : "Honda",
  "model" : "Civic",
  "type" : "LX",
  "year" : "2016",
  "price" : 18999,
  "inventoryCount" : 2
}
  ```

14.  Open the MongoDB application container (Pod) terminal window using the OpenShift Web UI.
  ![alt tag](https://raw.githubusercontent.com/ganrad/ose-fis-auto-dealer/master/mongodb-4.png)
15.  Log into the MongoDB client console and issue the following commands to verify the data has been persisted into the *'ose'* collection.
  ![alt tag](https://raw.githubusercontent.com/ganrad/ose-fis-auto-dealer/master/mongodb-5.png)
  You can also retrieve all the saved documents in MongoDB using the command below.

  ```
  db.ose.find()
  ```
  Exit the MongoDB console.
  
  ```
  quit()
  ```
16.  Open the FIS microservice container (Pod) terminal window using the OpenShift Web UI.  Then verify the mounted secrets in the '/etc/secrets-vol' directory.  See screenshot below.
  ![alt tag](https://raw.githubusercontent.com/ganrad/ose-fis-auto-dealer/master/secrets01.png)
