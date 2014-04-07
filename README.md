reconciling
===========
Put your API-keys in the appropiate *.properties key. Use the sample files to see how the files have to be structured.

**Be advised you must not push your api-key to a public repository!**

Freebase
---

Freebase (and Youtube API-keys) have to be obtained from the [google developer console](https://code.google.com/apis/console). There you need to enable (*YouTube Data API v3* and) *Freebase API*. To do so, navigate to "APIs & auth" --> "APIs" and switch the status of these APIs to **on**.
Now you are allowed to use you API-Key. To actually get the key, click on "Credentials" and create a new key for browser applications.
Copypaste it into the *freebase.properties* file like this

    API_KEY = your_api_key
	
in the same folder the `freebase.properties.sample` can be found.

Last.fm
---

Last.fm keys can be requested on [their website](http://www.last.fm/api). Simply follow their instructions.
Add both the api-key and the secret key to a file called *lastfm.properties* formatted like this

    API_KEY = your_api_key

Flickr
---

To get a Flickr API-key you need a YAHOO! account. (Create one, ) Log in and visit [the YAHOO! API page](https://secure.flickr.com/services/api/keys/) and click "Request an API Key".
Add both the api-key and the secret key to a file called *flickr.properties* formatted like this

    API_KEY = your_api_key
    SECRET_KEY = your_secret_key

