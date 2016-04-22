# OpenShift FIS microservice application : ose-fis-auto-dealer
This microservice application uses OpenShift FIS (Fuse Integration Services) tools and demonstrates how to build and deploy microservices in OpenShift Enterprise v3.1
The application exposes a RESTFul API with two *http* end-points.  This example application is a stripped down version of the original OpenShift FIS example posted 
by wei meilin - [jboss-fis-autodealer](https://github.com/jbossdemocentral/jboss-fis-autodealer)

## Steps for deploying *ose-fis-auto-dealer* microservice
1.  Fork this repository so that it gets added to your GitHub account.
2.  Download the template file (template object definition) into your master node.
  * Click on *kube-template.json*, then click on *Raw*.  Copy the http URL and use the CURL command to download the template file to your
  OpenShift master node (OR to the server where you have installed OpenShift client tools).
  ```
  $ https://raw.githubusercontent.com/<your GIT account name>/ose-fis-auto-dealer/master/kube-template.json
  ```
3.  Login to OpenShift 

  ```
  $ oc login -u user -p password
  ```
4.  Create a new project 

  ```
  $ oc new-project fis-auto-dealer
  ```
5.  Import the template file into your project 
  * Alternatively, you can import the template to the 'openshift' project using '-n openshift' option.  This would give all OpenShift users access to this 
  template definition.
  ```
  $ oc create -f kube-template.json
  ```
6.  Create the microservice application
  * This command kicks off the S2I build process in OpenShift.
  * Alternatively, you can use the OpenShift Web UI to create the application.
  * Remember to substitute your GIT account user name in the GIT http url below.
  ```
  $ oc new-app --template=carddemo --param=GIT_REPO=https://github.com/<your GIT account username>/ose-fis-auto-dealer.git
  ```
7.  Use the commands below to check the status of the build and deployment 
  * The build (Maven) might take a while (approx. 5-10 mins) to download all dependencies, build the code and then push the image into
  the integrated Docker registry.
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
8.  At this point, you should have successfully built an Apache Camel based RESTful microservice using OpenShift FIS tooling and deployed
the same to the OpenShift PaaS!
9.  Lastly, test the REST end-points using your browser. Substitute the correct values for route name, project name and 
openshift domain name as they pertain to your OpenShift project.
  * Test *'getVehicle'* end-point. The result of the REST API call should be JSON data. Vehicle numbers/IDs 
  which you can retrieve are vno01 ... vno05.  Substitute the exact vehicle ID you want to retrieve in the URL (below).
  ```
  http://route name-project name.openshift domain name/AutoDMS/getVehicle/vno01
  ```
  * Test *'availableVehicle'* end-point.  See an example below.
  ```
  http://route name-project name.openshift domain name/AutoDMS/availableVehicle/pricerange/20000/30000
  ```
