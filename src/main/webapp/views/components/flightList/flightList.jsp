<section class="w-full flex flex-col items-center justify-center py-8">
 
  <div class="w-full max-w-5xl relative overflow-hidden">
    <!-- Flecha izquierda -->
    <button id="flight-prev" aria-label="Anterior"
      class="hidden absolute left-0 top-1/2 -translate-y-1/2 z-10
             bg-black/40 hover:bg-black/70 text-white rounded-full
             w-10 h-10 md:w-12 md:h-12 flex items-center justify-center focus:outline-none">
      <svg class="w-5 md:w-7 h-5 md:h-7" fill="none" stroke="currentColor" stroke-width="2"
           viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M15 19l-7-7 7-7"/></svg>
    </button>

    <!-- Contenedor scrolleable -->
    <div id="flight-list-container"
         class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-8
                transition-all duration-500 overflow-x-auto scroll-smooth
                no-scrollbar w-full pb-6 pr-6 -mr-6 -mb-6">
      <!-- Cards por JS -->
    </div>

    <!-- Flecha derecha -->
    <button id="flight-next" aria-label="Siguiente"
      class="hidden absolute right-0 top-1/2 -translate-y-1/2 z-10
             bg-black/40 hover:bg-black/70 text-white rounded-full
             w-10 h-10 md:w-12 md:h-12 flex items-center justify-center focus:outline-none">
      <svg class="w-5 md:w-7 h-5 md:h-7" fill="none" stroke="currentColor" stroke-width="2"
           viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M9 5l7 7-7 7"/></svg>
    </button>
  </div>
</section>
 
<style>
  .no-scrollbar{ -ms-overflow-style:none; scrollbar-width:none; }
  .no-scrollbar::-webkit-scrollbar{ width:0; height:0; }
</style>
