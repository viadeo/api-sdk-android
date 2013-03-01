# Viadeo API SDK for Android

**This is BETA software, you can report bugs using the [support form](http://dev.viadeo.com/technical-support/).**

## Step 1 : Request an API Key

[Register a new application](http://dev.viadeo.com/documentation/authentication/request-an-api-key/) on this site to get a **Client Id** and a **Client Secret**.

## Step 2 : Download and install the Android SDK

* Install Eclipse if you don't have it already
* Install Android SDK, [See the documentation](http://developer.android.com/sdk/index.html)
* Install the Eclipse Plugin, [See the documentation](http://developer.android.com/sdk/eclipse-adt.html)
* For Emulator testing, create Virtual devices, [See the documentation](http://developer.android.com/guide/developing/devices/managing-avds.html)
* Clone the project or download the repository as a zip file

## Step 3 : Create a new Viadeo SDK Shared Library Project

Create a new Android Project for the Viadeo SDK. This project will be a **shared library project** for all of your Android applications that will use the Viadeo SDK. You need to create this project just one time for all of your projects. 

* Open Eclipse
* Create a new Android Project : File > New > Project > Android Project
* Set a project name, for example "ViadeoSDK"
* Select : "Create project from existing source"
* Specify the directory where you have unzip the Viadeo SDK
* Click "Finish"

## Step 4 : Add reference to the Viadeo SDK Shared Library Project

Now, you need to add a reference from your application to the Viadeo SDK Shared Library Project.

* Open "Properties" of your application project
* In "Library" section, click "Add" and select the Viadeo SDK Shared Library Project

## Step 5 : Add INTERNET permission

If it's not already done in your project, you must add the INTERNET permission in your **AndroidManifest** file. 

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

## Step 6 : Authentication

You need to provide to your users a system to connect to Viadeo. To do that, you just need to instantiate a Viadeo main object with your **Client Id** and **Client Secret** and call **authorize()**. Authorize method open a webpage in a dialog to prompt the user to login to his Viadeo account and grant the application to access to his account. This method is asynchronous and you have a callback interface for notifying the calling application when the authentication dialog has completed, failed, or been canceled.

```java
Viadeo viadeo = new Viadeo(this, VIADEO_CLIENT_ID, VIADEO_CLIENT_SECRET);
		
viadeo.authorize(new ViadeoOAuthListener() {

	@Override
	public void onViadeoOAuthComplete() {
	}

	@Override
	public void onViadeoOAuthCancel() {
	}

	@Override
	public void onViadeoOAuthError(int errorCode, String description, String failingUrl) {
	}

});
```

## Step 7 : Use the Graph API

When a user is authenticated, you can call the Viadeo Graph API. To make an asynchronous call, the best and easiest way, it's to create a **ViadeoRequest** object with these different parameters :

* requestCode : this code will be returned in ViadeoRequestListener
* graphPath: Path to resource in the Viadeo graph, e.g., to fetch data about the currently logged authenticated user, provide "/me", which will fetch https://api.viadeo.com/me
* params: Parameters for the request, could be null
* method: The HTTP method (GET, POST, PUT, DELETE)
* viadeo: the Viadeo main object
* listener: Callback interface for notifying the calling application when the request is completed or has failed

When you have created your **ViadeoRequest** object, you can execute this request with the **ViadeoRequester** object. You get the response from the server with the callback interface **ViadeoRequestListener**.

```java
ViadeoRequest viadeoRequest = new ViadeoRequest(REQUEST_CODE_ME, "/me", null, Method.GET, viadeo, new ViadeoRequestListener() {
	
	@Override
	public void onViadeoRequestError(int requestCode, String errorMessage) {
	}
	
	@Override
	public void onViadeoRequestComplete(int requestCode, String response) {
	}
});
new ViadeoRequester().execute(viadeoRequest);
```

If you want to manage your threads alone, you can also call the Viadeo Graph API synchronously with the method **request()** on the Viadeo main object.

```java
viadeo.request("/me", Method.GET, null);
```

## Step 8 : Logout

It's important to provide to your users a way to disconnect from Viadeo. To do that, you just need to call **logOut()** method on the Viadeo main object.

```java
// disconnect user
viadeo.logOut();
```

## Test if user is already logged in

To provide a more useful user experience and doesn't prompt for credentials all the time, you can use the **isLoggedIn()** method on the Viadeo main object.

```java
if(viadeo.isLoggedIn()) {
	// do something
}
```

## Viadeo login/logout button

If you want a Button to login and logout with the color of Viadeo, you can use the **ViadeoConnectButton** widget. It's work like a normal Android Button. In addition, you have just the **refreshState()** method to update the button in function of the user is logged in or not.

```java
ViadeoConnectButton button = new ViadeoConnectButton(context);
button.refreshState(viadeo);
```

You can also instantiate the **ViadeoConnectButton** directly in your xml layout file.

```xml
<com.viadeo.android.sdk.ViadeoConnectButton
    android:id="@+id/button"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content" />
```

## Logs

If you want to unactivate logs for production, just set :

```java
Viadeo.LOG = false;
```

## ViadeoRequest  examples

Here it is some **ViadeoRequest** examples to show the different HTTP methods with parameters.

Retrieves all tags that you have created :
```java
ViadeoRequest viadeoRequest = new ViadeoRequest(REQUEST_CODE, "/me/tags", null, Method.GET, viadeo, this);
```

Retrieve information from your Viadeo member's profile in language english :
```java
Bundle params = new Bundle();
params.putString("language", "en");
ViadeoRequest viadeoRequest = new ViadeoRequest(REQUEST_CODE, "/me", params, Method.GET, viadeo, this);
```

Retrieve a list of your first 5 contacts :
```java
Bundle params = new Bundle();
params.putString("limit", "5");
ViadeoRequest viadeoRequest = new ViadeoRequest(REQUEST_CODE, "/me/contacts", params, Method.GET, viadeo, this);
```

Retrieve a list of the last 5 members that consult your member’s profile :
```java
Bundle params = new Bundle();
params.putString("limit", "5");
ViadeoRequest viadeoRequest = new ViadeoRequest(REQUEST_CODE, "/me/visits", params, Method.GET, viadeo, this);
```

Publish a new status with the message "hello from Android Viadeo SDK" :
```java
Bundle params = new Bundle();
params.putString("message", "hello from Android Viadeo SDK");
ViadeoRequest viadeoRequest = new ViadeoRequest(REQUEST_CODE, "/status", params, Method.POST, viadeo, this);
```

Add the tag "Project Manager" to the member that had the ID "azertyuiopqsdfghjklmwxcvbn" :
```java
Bundle params = new Bundle();
params.putString("contact_id", "azertyuiopqsdfghjklmwxcvbn");
params.putString("tag", "Project Manager");
ViadeoRequest viadeoRequest = new ViadeoRequest(REQUEST_CODE, "/me/tags", params, Method.POST, viadeo, this);
```

Add a new professional experience to your profile at the company "Viadeo", with the position of "Software Engineer" from "2008" to "2011" :
```java
Bundle params = new Bundle();
params.putString("company", "Viadeo");
params.putString("position", "Software Engineer");
params.putString("from", "2008");
params.putString("to", "2011");
ViadeoRequest viadeoRequest = new ViadeoRequest(REQUEST_CODE, "/me/career", params, Method.POST, viadeo, this);
```

Change some information on your member's profile, set your headline to "Software Engineer, Viadeo" and your interests to "Scuba diving, Base jumping, Chess" :
```java
Bundle params = new Bundle();
params.putString("headline", "Software Engineer, Viadeo");
params.putString("interests", "Scuba diving, Base jumping, Chess");
ViadeoRequest viadeoRequest = new ViadeoRequest(REQUEST_CODE, "/me", params, Method.PUT, viadeo, this);
```

Delete the tag "Project Manager" to the member that had the ID "azertyuiopqsdfghjklmwxcvbn" :
```java
Bundle params = new Bundle();
params.putString("contact_id", "azertyuiopqsdfghjklmwxcvbn");
params.putString("tag", "Project Manager");
ViadeoRequest viadeoRequest = new ViadeoRequest(REQUEST_CODE, "/me/tags", params, Method.DELETE, viadeo, this);
```