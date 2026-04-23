import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  images: {
    remotePatterns: [
      {
        protocol: "https",
        hostname: "**"
      }
    ]
  },
  async redirects() {
    return [
      {
        source: "/consultar",
        destination: "/",
        permanent: false
      }
    ];
  },
  async rewrites() {
    return [
      {
        source: "/insertar",
        destination: "http://localhost:8080/insertar"
      },
      {
        source: "/configuracion-campos",
        destination: "http://localhost:8080/configuracion-campos"
      },
      {
        source: "/muestra/:path*",
        destination: "http://localhost:8080/muestra/:path*"
      },
      {
        source: "/muestra_datos",
        destination: "http://localhost:8080/muestra_datos"
      },
      {
        source: "/editar/:path*",
        destination: "http://localhost:8080/editar/:path*"
      },
      {
        source: "/error",
        destination: "http://localhost:8080/error"
      },
      {
        source: "/css/:path*",
        destination: "http://localhost:8080/css/:path*"
      },
      {
        source: "/js/:path*",
        destination: "http://localhost:8080/js/:path*"
      },
      {
        source: "/img/:path*",
        destination: "http://localhost:8080/img/:path*"
      },
      {
        source: "/upload-jobs/:path*",
        destination: "http://localhost:8080/api/upload-jobs/:path*"
      },
      {
        source: "/api/insertar",
        destination: "http://localhost:8080/api/insertar"
      },
      {
        source: "/api/insertar/:path*",
        destination: "http://localhost:8080/api/insertar/:path*"
      },
      {
        source: "/api/backend/:path*",
        destination: "http://localhost:8080/api/:path*"
      },
      {
        source: "/api/descargar/:path*",
        destination: "http://localhost:8080/api/descargar/:path*"
      },
      {
        source: "/eliminar/:path*",
        destination: "http://localhost:8080/eliminar/:path*"
      },
      {
        source: "/eliminar-multiples",
        destination: "http://localhost:8080/eliminar-multiples"
      },
      {
        source: "/mantenimiento/:path*",
        destination: "http://localhost:8080/mantenimiento/:path*"
      },
      {
        source: "/configuracion-campos/:path*",
        destination: "http://localhost:8080/configuracion-campos/:path*"
      }
    ];
  }
};

export default nextConfig;
