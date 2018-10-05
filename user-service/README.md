# user-service

### Generate key store
`Note:` Override for different env!
~~~
keytool -genkeypair -alias <ALIAS> -keyalg RSA -keysize 2048 -keypass <PASSWORD> -keystore /home/keystore.jks -storepass <PASSWORD>
~~~
Then change
```
coopel:
  auth:
    keystore:
      path: <PATH_TO_KEYSTORE>
      secret: <PASSWORD>
      key-alias: <ALIAS>
```
