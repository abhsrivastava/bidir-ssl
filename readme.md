# Bi directional SSL handshake with http4s

## Certificate generation process

1. Generate a certificate for the server

keytool -genkeypair -alias serverkey -keyalg RSA -dname "CN=foo.com,OU=foo,O=foo,L=foo,S=TX,C=US" -keypass secret -keystore server.jks -storepass secret
keytool -importkeystore -srckeystore server.jks -destkeystore server.jks -deststoretype pkcs12

2. Generate a certificate for the client

keytool -genkeypair -alias clientkey -keyalg RSA -dname "CN=bar.com,OU=bar,O=bar,L=bar,S=TX,C=US" -keypass secret -keystore client.jks -storepass secret
keytool -importkeystore -srckeystore client.jks -destkeystore client.jks -deststoretype pkcs12

3. Now that we have the certificates, we need to export the public key of each. 

keytool -exportcert -alias serverpublickey -file server-public.cer -keystore server.jks -storepass secret
keytool -exportcert -alias clientpublickey -file client-public.cer -keystore client.jks -storepass secret

4. Now that the public keys are generated these need to be exchanged. The server must have clients public key and the client must have servers public key

keytool -importcert -keystore server.jks -alias clientcert -file client-public.cer -storepass secret -noprompt
keytool -importcert -keystore client.jks -alias clientcert -file server-public.cer -storepass secret -noprompt

5. Now the client.jks has the clients certificate and the servers public key. Copy this in client resource folder

cp client.jks client/src/main/resources

6. Now the server.jks has the server certificate and the clients public key. copy this into server resource folder

cp server.jks server/src/main/resources

