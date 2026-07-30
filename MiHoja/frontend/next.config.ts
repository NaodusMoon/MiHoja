import type { NextConfig } from "next";

const backendBaseUrl = process.env.API_BASE_URL ?? "http://localhost:8080";

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
        destination: `${backendBaseUrl}/insertar`
      },
      {
        source: "/configuracion-campos",
        destination: `${backendBaseUrl}/configuracion-campos`
      },
      {
        source: "/muestra/:path*",
        destination: `${backendBaseUrl}/muestra/:path*`
      },
      {
        source: "/muestra_datos",
        destination: `${backendBaseUrl}/muestra_datos`
      },
      {
        source: "/editar/:path*",
        destination: `${backendBaseUrl}/editar/:path*`
      },
      {
        source: "/error",
        destination: `${backendBaseUrl}/error`
      },
      {
        source: "/css/:path*",
        destination: `${backendBaseUrl}/css/:path*`
      },
      {
        source: "/js/:path*",
        destination: `${backendBaseUrl}/js/:path*`
      },
      {
        source: "/img/:path*",
        destination: `${backendBaseUrl}/img/:path*`
      },
      {
        source: "/upload-jobs/:path*",
        destination: `${backendBaseUrl}/api/upload-jobs/:path*`
      },
      {
        source: "/api/insertar",
        destination: `${backendBaseUrl}/api/insertar`
      },
      {
        source: "/api/insertar/:path*",
        destination: `${backendBaseUrl}/api/insertar/:path*`
      },
      {
        source: "/api/backend/:path*",
        destination: `${backendBaseUrl}/api/:path*`
      },
      {
        source: "/api/descargar/:path*",
        destination: `${backendBaseUrl}/api/descargar/:path*`
      },
      {
        source: "/eliminar/:path*",
        destination: `${backendBaseUrl}/eliminar/:path*`
      },
      {
        source: "/eliminar-multiples",
        destination: `${backendBaseUrl}/eliminar-multiples`
      },
      {
        source: "/mantenimiento/:path*",
        destination: `${backendBaseUrl}/mantenimiento/:path*`
      },
      {
        source: "/configuracion-campos/:path*",
        destination: `${backendBaseUrl}/configuracion-campos/:path*`
      }
    ];
  }
};

export default nextConfig;
