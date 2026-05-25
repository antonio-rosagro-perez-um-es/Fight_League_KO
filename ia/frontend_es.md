# Frontend

Para la creación del frontend se ha decidido usar como tecnologias Angular junto con Material icons. Debido a su compatibilida usaremos typescript junto con angular. En caso de necesitar otras librerias pueden usarse si,
se pide primero permiso durante la fase de planificacion.

La aplicacion consiste en una aplciacion donde poder ver los combos oficiales de los fighters del juego 2XKO, junto con la organizacion de torneos por parte de la comunidad y el uso de la aplicacion para el guardado de combos,
pubicacion y compration de los mismos. Las vistas seran explicadas en los siguientes apartados.

Utiliza la carpeta de fronted para el desarrollo, ademas de hacer uso de `npx autoskills` para instalar las skills necesarias.

El contenido multimedia que te sea posible recabar almacenalo mediante una correcta arquitectura de ficheros para que pueda ser posible utilizarla, en caso de no poder recabar contenido multimedia deja solo las rutas y crea un fichero aparte llamado pending.md donde se indique todas aquellas operaciones o enlaces rotos que queden por el proyecto.

Si consideras necesario ve creando commits durante la creacion para ir guardando el proceso, utiliza buensa prácticas como nombres descriptivos (add, create, fix, etc.).

En caso de necesitar informacion sobre que tiene permitido hacer cada tipo de usuario consulta los ficheros spring-security.md y /Fight_League_KO/backend/src/main/java/FightLeagueKO/security/SecurityConfig.java. En caso de duda preguntar.

Como funcionalidad general se busca que cualquier combo mostrado pueda ser traducido entre notacion de texto a imagenes como en <https://2xkombo.gg/> donde tenemos ![alt text](img/image_2.png).

## Heather

El heather tiene cuatro tipos distintos, de los cuales dos son muy similares.

1. El usuario sin registrar dispondra de un heather con los campos Inicio, fighters, Estadisticas, Ranking, Torenos y calendario alineados a la izquierda y alineados a la izquierda logIn y register.
![alt text](img/img_heather.png)
2. El usuairo registrado sera igual que el no registrado con la diferencia de que en lugar de iniciar sesion y registrarse tendra unsu icono de usuario el cual sera un boton desplegable con las opciones de ir a su perfil y el boton de cerrar sesion. En los campos de la izquierda ademas tendra la seccion de combos de la comunidad.
3. El usuario registrado organizador tendra los mismos campos que un usuario registrado.
4. El menu del usuario registrado dispondra de los campos para acceder a manejar el sistema, estos campos son: combo, usuario, juegos, equipos, toorneos, situados a la izquierda mientras que a la derecha aparecera un icono con la imagen del usuario administrador.
![alt text](img/image_heather_1.png)

## Footer

El footer contendra los enalces a las redes sociales de twitter, instagram, un contact me a una direccion de correo plantilla que sera modificada mas adelante, y un suppor me. Previo a los enlaces aparecera un menaje advirtiendo que no se tiene derechos sobre los productos y pertenecen a riot games que es solo para fines academicos. Añade un sitemap de forma simplfifcada.

![alt text](img/footer.png)

## Registro - Inicio de sesion

Las vistas para el registro de un usuario seran formularios tipicos que satisfagan los endpoints para la creacion e inicio de sesion de usaurio. Intenta estilizarlo un poco.

## Perfil usuario

### Organizdor - usuario registrado

En el perfil se vera una imagen el nick y estadisticas personales como victorios/derrotas como en <https://2xkombo.gg/player/m80-hikari-1803850?season=season_0>, junto con un boto para la modificacion de parametros permitidos. 

## Principal/Inicio

Tenemos 3 tipos de visata distinta en funcion del tipo de ususario que seamos

### No registrado

En la vista principal se quiere tener un grid de fighters ordenados por fecha de creacion, se busca un diseño similar al del sitio <https://www.streetfighter.com/6/es-es/character>, donde al hacer click sobre alguno de los fighters nos lleve hasta su ficha de personaje. Tambien se busca ese efecto de que al pasar el raton se resalte el personaje.  

### Usuario registrado

El usaurio registrado tendra una lista con sus ultimas partidas jugadas en caso de no tener partidas jugadas se mostrara un mensaje indicando de forma motivadora que es momento de unirse a un torneo, junto con un boton que lleve a torneos. Se mostrara con un fondo verde claro aquellas partidas ganadas por el usurio y en rojo las perdidas. Se mostrara mediante imagenes circualres pequeñas

![alt text](img/inicio_registrado.png)

### Usuario organizador

Igual que el registrado

### Usuario admin

Mostrara una lista de los distiontos accesos que tiene en heather para poder acceder a ellos.

## Fighters

La vista de un perosonaje se divide en dos tipos de usuarios.

### Administrador

Tendra un texto indicando la seccion en la que esta junto con un boton para la creación de nuevos personajes situado a la derecha encima de una tabla con los principales atributos de un fighter, solo los mas relevantes como id, nombre, tipo, slug, delete, como columna y asi cada fila representa un fighter, al final de cada fila habra una serie de botones que permitan editar, borrar, restauray  ver toda la información. Al principio de cada fila habra un checkbox que permitira borrar varios fighters a la vez lanzado varias peticiones de borrado.

![alt text](img/fighter_admin.png)

La creacion de un nuevo personaje sera mediante un formulario que contara con los campos necesarios para satisfacer el endpoint para la creacion de backend/src/main/java/FightLeagueKO/fighter/controller/FighterController.java, por su parte la modificacion hara lo mismo pero mostrara toda la información y solo dejara editar los campos necesario para la actualizacion.

Los formularios seran mostrados mediante ventanas emergentes y seran enviados cuando el usuario presione el boton de enviar.

### No registrado - registrado - organizador

Mostrara un display similar al de <https://www.streetfighter.com/6/es-es/character/cammy> manteniendo solo la informacion que coincida con la de los campos del fighter, como son la descripcion o los gustos y añadiendo otros ocmo el tipo o la región. Se busca uan distribucion similar con información repartida a derecha e izquierda con una imagen del personaje en medio y un submenu encima de la información colocada a la derecha.

El submenu tendra dos entradas una llamada info que es la vista principal usada para volver y otra llamada combos officiales usada para mostrar los combos officiales de este personaje. El submenu de combos busca tener una ditribucion similar a <https://www.streetfighter.com/6/es-es/character/cammy/movelist>

Cuando digo que se busca en la vista princiapl del personaje un display simialr al de <https://www.streetfighter.com/6/es-es/character/cammy> tambien me refiero a tener debajo un mini grid con otros personajes.
![alt text](img/image_1.png)

## Estadisticas

La sección de estadisticas estara dividida en 2 secciones si el usuario no esta registrado y en 3 si el usuario si lo esta.

### Administrador

Igual que en fighters.

### registrado - organizador

Primero mostrara los personajes más jugados y con mejor winrate (o solo una de las dos) del usuario, despues mostrara los personajes con mejor winrate y finalmente los equipos con mejor winrate. Para el estilo de esta vista se quiere una similar a la de <https://2xkombo.gg/characters>. Se mostrara en cada caso los 10 mejores.

### no registrado

Igual que registrado pero sin mostrar estadisticas del usuario solo los dos siguientes.

## Ranking

El ranking mostrara los usuarios con mayor numero de torneos ganados, podiendo hacer click para ver sus perfiles. No habra vista de ranking para administrador. Se busca un formato como en esta página
<https://2xkombo.gg/rankings>, añadir una puntuacion en el backend para el usuario para que dependiendo de su posicion en un torneo se le asigne una puntuacion de 10 a 1 siendo esta en fucnion de su posicion en los torneos, es decir ganar un torneo (ser primero) son 10 puntos mientras que quedar el 10 será 1 punto, el resto recibira 0.

## Calendar

La vista de calendario mostrara un calendario donde se resaltara el dia actual y se mostrara los proximos torneos en formato calendario valiendose de la lista de torneos y ordenandolos por fecha para mostrarlos de forma ordenada. Se busca una vista similar a <https://2xkombo.gg/tournament-calendar>

## Torneo

### Administrador

Igual que en fighters

### Otros usuarios

Desde la vista de torneos se mostrara un listado de torneos en forma de columna ordenados de más proxmimo a mas lejado como en <https://2xkombo.gg/tournaments>, al hacer click a un torneo se habrira una vista donde el usuario podra ver información publica del torneo como plazas restantes, cuando termina el proceso de incripción, su estado y lo más importante será un boton que permita unirse al torneo si es posible. Al unirse a un torneo el boton cambia por uno que permite salir del torneo.

En caso de pulsar el boton de unirse un usuario no registrado sera llevado a la vista de registro de usuario. Esto sucedera con todas las acciones que no tiene permitidas

Para un usuario organizador primero se mostran en una seccion separada los torneos que organizada, al hacer click sobre ellos aparecera una nueva vista flotante con los campos de información y la posibilidad de realizar acciones que este acutorizado como es la modificacion, cierre de inscripciones, cancelacion, etc. Para ver todas las acciones ver backend/src/main/java/FightLeagueKO/tournament/controller/TournamentController.java
