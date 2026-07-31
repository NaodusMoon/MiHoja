import type { Metadata } from "next";
import { IBM_Plex_Sans } from "next/font/google";

import "./globals.css";

const bodyFont = IBM_Plex_Sans({
  variable: "--font-body",
  subsets: ["latin"]
});

export const metadata: Metadata = {
  title: "MiHoja Dashboard",
  description: "Frontend Next.js para MiHoja sobre backend Spring Boot",
  icons: {
    icon: "/logo-mihoja.png",
    shortcut: "/logo-mihoja.png",
    apple: "/logo-mihoja.png"
  }
};

export default function RootLayout({ children }: Readonly<{ children: React.ReactNode }>) {
  return (
    <html lang="es">
      <body className={bodyFont.variable}>{children}</body>
    </html>
  );
}
