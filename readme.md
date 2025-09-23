# 🧠 Estructura de Carpetas Explicada

```bash
📦 app-web/
├── public/
│   └── assets/
│       ├── icons/
│       └── images/
├── config/
├── jsp/
├── src/
│   ├── scripts/
│   │   ├── auth/
│   │   ├── utils/
│   │   └── validators/
│   ├── views/
│   │   ├── components/
│   │   │   ├── button/
│   │   │   ├── modal/
│   │   │   └── sidebar/
│   │   ├── footer/
│   │   ├── header/
│   │   └── main/
│   └── styles.css
│   └── index.html
├── .gitignore
└── readme.md
```

---

## 📁 `public/assets/`

Contiene archivos **estáticos** que se sirven tal cual al navegador.

* `icons/`: íconos tipo `.svg`, `.png`, `.ico`.
* `images/`: imágenes usadas en la UI.

Usado directamente en el HTML:

```html
<img src="/assets/images/banner.jpg" />
```

---

## 📁 `config/`

Archivos de configuración del sitio.

* `robots.txt`: controla el acceso de los motores de búsqueda.

---

## 📁 `jsp/`

Espacio para tus **Java Server Pages** (a futuro).

* `includes/`: fragmentos reusables como headers, footers.
* `pages/`: vistas dinámicas completas.

---

## 📁 `src/`

Código fuente **editable** del frontend.

### 📁 `scripts/`

Organización por tipo de lógica:

* `auth/`: login, sesión, etc.
* `utils/`: helpers reutilizables.
* `validators/`: validaciones de formularios y datos.

### 📁 `views/`

Templates HTML divididos por sección visual:

* `footer/`, `header/`, `main/`: componentes visuales reusables.

* `components/`: componentes reutilizables pequeños y modulares como `button`, `modal`, `sidebar`, etc.

> Cada componente dentro de `views/components/` debe tener su propio scope con **`HTML`, `CSS` y `JS`** dentro del mismo directorio. Esto permite encapsular la lógica, estilo y estructura de forma clara y reutilizable:
>
> ```bash
> 📁 button/
> ├── button.html
> ├── button.css
> └── button.js
> ```
>
> En el caso de usar **TailwindCSS**, estos estilos pueden ser mínimos o inexistentes, ya que las clases utilitarias de Tailwind resuelven gran parte del layout y estilos visuales. Sin embargo, si necesitás customizaciones específicas, cada componente puede tener su propio archivo `.css` para override o clases utilitarias personalizadas.

### 📄 `index.html`

Archivo de entrada principal del sitio.

---

## Archivos raíz

* `.gitignore`: excluye archivos temporales o de entorno.
* `readme.md`: documentación del proyecto.

---

## 🔮 Escalabilidad

Esta estructura permite:

* Usar TailwindCSS de forma nativa sin CSS adicional.
* Separar frontend estático de backend dinámico (JSP).
* Modularizar el JS y HTML sin dolor.
* Documentar y testear de forma clara.

---

## 🧱 Analogía de Arquitectura

* `public/assets/`: decoración y materiales visuales.
* `src/`: planos, herramientas y obreros.
* `views/components/`: muebles modulares reutilizables (cada uno con su plano, acabado y comportamiento).
* `jsp/`: habitaciones dinámicas.
* `config/`: reglas del edificio.

Todo separado, limpio y listo para escalar.
