# Transferencia_archivos_nube
Sistema de transferencia de archivos desarrollado en la nube.

Lo primero a realizar, es crear un certificado ssh en nuestro servidor ubuntu, así como
verificarlo, pues es indispensable para que el sistema funcione, se necesita que esté en ambas partes, pues es nuestro
certificado de conexión segura.

Primero ejecutaremos el servidor en la máquina virtual

![image](https://github.com/AngelVelascoH/Transferencia_archivos_nube/assets/86260733/d7ab4e2d-1923-41b5-bfba-83d93463c647)

El servidor comenzará a escuchar:
Ejecutamos el cliente PUT primero, y le pedimos que ponga el archivo local

![image](https://github.com/AngelVelascoH/Transferencia_archivos_nube/assets/86260733/ee865887-8cf1-4a65-86ad-86a5c44a91df)

![image](https://github.com/AngelVelascoH/Transferencia_archivos_nube/assets/86260733/32573d31-2d80-4318-a5e1-5e161f259ffa)

Indica que el archivo fue enviado correctamente, interrumpamos el servidor, para
verificar que el archivo se encuentre ahora en el server

![image](https://github.com/AngelVelascoH/Transferencia_archivos_nube/assets/86260733/cc243cf9-7ca1-4aae-b99b-e5c341942f46)

Y ahora ejecutamos el cliente GET, donde le pediremos el archivo remoto:

![image](https://github.com/AngelVelascoH/Transferencia_archivos_nube/assets/86260733/3ded818c-a7db-4224-89f3-b2b2da1d898a)

![image](https://github.com/AngelVelascoH/Transferencia_archivos_nube/assets/86260733/803df16b-e756-4897-967e-bbb9e6303762)

 El archivo ahora se encuentra correctamente en nuestra máquina local.









 




