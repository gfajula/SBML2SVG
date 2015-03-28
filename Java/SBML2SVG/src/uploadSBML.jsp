<!DOCTYPE html>
<html lang="es">
<head>
<meta charset="utf-8"/>
<style type="text/css">
html {
  font-family: arial;
  font-size: 13px;
}

label {
  display: block;  
  font-weight: bold;
  min-width: 100px;
  width: 100px;
}

input[type='text'], input[type='email'] {  
  border: 1px  solid gray;
  width: 400px;
}


</style>
<title>Upload an SBML file</title>
</head>
<body>


<form method="POST" action="doUploadSBML.php" enctype="multipart/form-data">
  
  <label for="autor">Autor</label>
  <input type="text" name="autor" id="autor" autofocus placeholder="nombre del autor"/>
  <br/>
  <label for="nombre">Título</label>
  <input type="text" name="nombre" id="nombre" placeholder="título del diagrama"/>
  <br/>
  <label for="descripcion">Descripción</label>
  <input type="text" name="descripcion" id="descripcion" placeholder="descripción del diagrama"/>
  <br/>

  <label for="email">E-Mail</label>
  <input type="email" name="email" id="email" placeholder="dirección de E-Mail del autor" required />
  <br/>
  <label for="sbmlFile">Fichero SBML</label>
  <input type="file" name="sbmlFile" id="sbmlFile" value="Abrir archivo..." required/>

  <br/>

  <br/>

  <input type="submit" value="Enviar fichero"  />
</form>

</body> 
</html> 
