# modularwallets-android-sdk

## Installation
Add the maven repository to your gradle file. It's suggested that load settings from `local.properties`:
```gradle
repositories {
	...
	maven { 
        	Properties properties = new Properties()
		// Load local.properties.
        	properties.load(new File(rootDir.absolutePath + "/local.properties").newDataInputStream())
			
		url properties.getProperty('mwsdk.maven.url')
		credentials {
        		username properties.getProperty('mwsdk.maven.username')
        		password properties.getProperty('mwsdk.maven.password')
		}
	}
}
```
Add the maven setting values in `local.properties` file:
```properties
mwsdk.maven.url=https://maven.pkg.github.com/circlefin/modularwallets-android-sdk
mwsdk.maven.username=<GITHUB_USERNAME>
# Classic personal access token with `read:packages` scope — GitHub Packages Gradle registry authenticates classic PATs only (fine-grained PATs are not supported here).
# For SSO-enforced orgs, also authorize the PAT for that org — see GitHub Packages auth docs.
mwsdk.maven.password=<GITHUB_PAT> 

```

Add the dependency:

```gradle
dependencies {
	implementation 'circle.modularwallets:core:version'
}
```
