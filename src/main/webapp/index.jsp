<!DOCTYPE html>
<html lang="es">
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>Volando Index</title>
	<script src="https://cdn.tailwindcss.com"></script>
	<script>
    tailwind.config = {
      theme: {
        extend: {
          colors: { brand: "#0B4C73" }
        }
      }
    }
	</script>
	<link rel="stylesheet" href="styles.css">
</head>
<body class="bg-brand-900 min-h-screen flex flex-col">
	<!-- Header dinámico -->
	<div id="header-container"></div>

	<!-- Contenido principal: paquetes y vuelos -->
	<main class="flex-1 container mx-auto px-4 py-8 flex flex-col">
		<div id="paquetes" class="w-full opacity-0 translate-y-8 transition-all duration-700 mb-8"></div>
		<div id="vuelos" class="w-full opacity-0 translate-y-8 transition-all duration-700"></div>
	</main>

	<!-- Footer dinámico (UNICO contenedor) -->
	<div id="footer-container" class="mt-auto"></div>

	<!-- carga principal de scripts -->
	<script src="index.js"></script>
</body>
</html>
	<div id="footer-container" class="mt-auto"></div>

	<!-- script del menú derecho (necesario para inyección) -->
	<script src="src/views/components/leftPanel/leftPanel.js"></script>
 	<script src="/public/index.js"></script>
</body>
</html>
