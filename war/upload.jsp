<html>
<head>
	<title>stragengine</title>
	<meta http-equiv="content-type" content="text/html; charset=UTF-8">
</head>
<body>
	<h1>stragengine</h1>
	<h2>File upload</h2>
	<form action="<%= request.getAttribute("requestUri") %>" method="post" enctype="multipart/form-data">
		<input type="file" name="name">
		<input type="submit" value="upload">
	</form>
</body>
</html>