import { Link } from 'react-router'
import { GraduationCap, Github, Instagram, MessageCircle } from 'lucide-react'

// "Contribuír" se renderiza manualmente en col B de navegación
const internalLinks = [
    { label: 'Inicio', href: '/home' },
    { label: 'Cursos', href: '/cursos' },
    { label: 'Comunidad', href: '/comunidad' },
    { label: 'Biblioteca', href: '/biblioteca' },
]

const externalLinks = [
    { label: 'Autogestión', href: 'https://autogestion.utn.edu.ar' },
    { label: 'Moodle', href: 'https://moodle.utn.edu.ar' },
]

const socialLinks = [
    { label: 'GitHub', icon: Github, href: 'https://github.com/utnet' },
    { label: 'Instagram', icon: Instagram, href: 'https://instagram.com/utnet' },
    { label: 'Discord', icon: MessageCircle, href: 'https://discord.gg/utnet' },
]

const linkClass = 'text-sm text-muted-foreground hover:text-foreground transition-colors duration-150'

export function Footer() {
    return (
        <footer className="border-t border-border/50 bg-background shrink-0">
            <div className="max-w-7xl mx-auto px-6 py-6">
                <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
                    {/* Col 1: Logo + tagline + socials */}
                    <div className="flex flex-col gap-3">
                        <div className="flex items-center gap-2">
                            <div className="w-8 h-8 rounded-lg bg-primary/10 flex items-center justify-center">
                                <GraduationCap size={16} className="text-primary" />
                            </div>
                            <span className="font-bold text-base tracking-tight">UTNet</span>
                        </div>
                        <p className="text-sm text-muted-foreground leading-relaxed max-w-xs">
                            La red de la comunidad universitaria. Foros, apuntes, reseñas y mucho más, en un solo lugar.
                        </p>
                        <div className="flex items-center gap-2">
                            {socialLinks.map(({ label, icon: Icon, href }) => (
                                <a
                                    key={label}
                                    href={href}
                                    target="_blank"
                                    rel="noopener noreferrer"
                                    aria-label={label}
                                    className="w-8 h-8 rounded-lg flex items-center justify-center text-muted-foreground hover:text-foreground hover:bg-secondary transition-all duration-150"
                                >
                                    <Icon size={16} />
                                </a>
                            ))}
                        </div>
                    </div>

                    {/* Col 2: Navegación — 2 sub-columnas (espejo de la sidebar) */}
                    <div className="flex flex-col gap-3">
                        <h3 className="text-xs font-semibold text-foreground tracking-wide uppercase">Navegación</h3>
                        <div className="grid grid-cols-2 gap-x-6 gap-y-1.5">
                            {/* Sub-col A: páginas principales */}
                            <div className="flex flex-col gap-1.5">
                                {internalLinks.map(({ label, href }) => (
                                    <Link key={href} to={href} className={linkClass}>
                                        {label}
                                    </Link>
                                ))}
                            </div>
                            {/* Sub-col B: Contribuír + sitios externos */}
                            <div className="flex flex-col gap-1.5">
                                <Link to="/contribuir" className={linkClass}>
                                    Contribuír
                                </Link>
                                {externalLinks.map(({ label, href }) => (
                                    <a
                                        key={href}
                                        href={href}
                                        target="_blank"
                                        rel="noopener noreferrer"
                                        className={linkClass}
                                    >
                                        {label} ↗
                                    </a>
                                ))}
                            </div>
                        </div>
                    </div>

                    {/* Col 3: Sobre UTNet */}
                    <div className="flex flex-col gap-3">
                        <h3 className="text-xs font-semibold text-foreground tracking-wide uppercase">Sobre UTNet</h3>
                        <p className="text-sm text-muted-foreground leading-relaxed">
                            UTNet es un proyecto open source creado por estudiantes de la Universidad Tecnológica Nacional (UTN) para centralizar los recursos y la vida universitaria.
                        </p>
                        <p className="text-sm text-muted-foreground leading-relaxed">
                            Nació de la necesidad de contar con una plataforma moderna, accesible y construida por y para la comunidad estudiantil.
                        </p>
                    </div>
                </div>
            </div>

            {/* Bottom bar */}
            <div className="border-t border-border/30">
                <div className="max-w-7xl mx-auto px-6 py-3 flex flex-col sm:flex-row items-center justify-between gap-2">
                    <span className="text-xs text-muted-foreground">© 2025 UTNet</span>
                    <span className="text-xs text-muted-foreground text-center">
                        Creado por y para estudiantes de UTN
                    </span>
                </div>
            </div>
        </footer>
    )
}
