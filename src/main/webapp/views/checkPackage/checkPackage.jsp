<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="utf-8"/>
  <meta name="viewport" content="width=device-width,initial-scale=1"/>
  <title>Mis Paquetes (Aerolínea)</title>
  <script src="https://cdn.tailwindcss.com"></script>
  <script>tailwind.config={theme:{extend:{colors:{brand:"#0B4C73"}}}};</script>
</head>
<body class="bg-gray-100 min-h-screen flex flex-col">
  <div id="header"></div>

  <main class="flex-1 container mx-auto px-4 py-8">
    <div class="grid grid-cols-1 lg:grid-cols-4 gap-8 max-w-7xl mx-auto">
      <aside id="rightMenu" class="hidden lg:block"></aside>
      <section class="lg:col-span-3 space-y-6">
        <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
          <h1 class="text-2xl font-bold text-brand">Paquetes de mi aerolínea</h1>
          <div class="flex flex-wrap gap-2">
            <a href="../createPackage/createPackage.jsp" class="px-4 py-2 bg-brand text-white text-sm rounded-lg hover:brightness-110">
              + Crear paquete
            </a>
            <button id="btn-refresh" class="px-4 py-2 border text-sm rounded-lg hover:bg-white">
              Recargar
            </button>
          </div>
        </div>

        <div id="cp-alert" class="hidden p-4 rounded bg-yellow-100 text-yellow-800 text-sm"></div>

        <div id="cp-list" class="grid gap-6 grid-cols-1 md:grid-cols-2 xl:grid-cols-3">
          <!-- cards -->
        </div>
      </section>
    </div>
  </main>

  <div id="footer"></div>

  <script src="../components/rightMenu/rightMenu.js"></script>
  <script src="../components/leftPanel/leftPanel.js"></script>
  <script src="checkPackage.js"></script>
</body>
</html>
