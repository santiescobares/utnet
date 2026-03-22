import { useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router'
import {
    BookOpen,
    ChevronDown,
    ExternalLink,
    GitFork,
    Globe,
    Home,
    Library,
    LogOut,
    Users,
    X,
} from 'lucide-react'
import { cn } from '@/lib/utils'
import { useAuthStore } from '@/store/authStore'
import { authService } from '@/services/auth.service'
import { UserAvatar } from '@/components/ui/UserAvatar'
import type { Role } from '@/types/user.types'

interface SidebarProps {
    isOpen: boolean
    onClose: () => void
}

interface NavLink {
    kind: 'link'
    label: string
    icon: React.ElementType
    href: string
}

interface NavExpandable {
    kind: 'expandable'
    label: string
    icon: React.ElementType
    children: { label: string; href: string }[]
}

type NavItem = NavLink | NavExpandable

const ROLE_LABELS: Record<Role, string> = {
    NEW_USER: 'Nuevo usuario',
    CONTRIBUTOR_1: 'Colaborador',
    CONTRIBUTOR_2: 'Colaborador Avanzado',
    CONTRIBUTOR_3: 'Colaborador Experto',
    ADMINISTRATOR: 'Administrador',
}

const topNav: NavItem[] = [
    { kind: 'link', label: 'Inicio', icon: Home, href: '/home' },
    { kind: 'link', label: 'Cursos', icon: BookOpen, href: '/cursos' },
    { kind: 'link', label: 'Comunidad', icon: Users, href: '/comunidad' },
    { kind: 'link', label: 'Biblioteca', icon: Library, href: '/biblioteca' },
    {
        kind: 'expandable',
        label: 'Sitios Oficiales',
        icon: Globe,
        children: [
            { label: 'Autogestión', href: 'https://autogestion.utn.edu.ar' },
            { label: 'Moodle', href: 'https://moodle.utn.edu.ar' },
        ],
    },
]

const navItemBase =
    'flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium text-white/90 transition-all duration-150 w-full text-left'

export function Sidebar({ isOpen, onClose }: SidebarProps) {
    const location = useLocation()
    const navigate = useNavigate()
    const { user, logout } = useAuthStore()
    const [isOfficialExpanded, setIsOfficialExpanded] = useState(false)

    const isActive = (href: string) => location.pathname === href

    const handleLogout = async () => {
        onClose()
        try {
            await authService.logout()
        } catch {
            // Si el backend no responde, igual limpiamos la sesión local
        } finally {
            logout()
            navigate('/')
        }
    }

    return (
        <>
            {/* Mobile backdrop — z-[55] sits above navbar (z-50) but below sidebar (z-[60]) */}
            {isOpen && (
                <div
                    className="fixed inset-0 bg-black/40 z-[55] md:hidden transition-opacity duration-200"
                    onClick={onClose}
                    aria-hidden="true"
                />
            )}

            {/* Sidebar panel */}
            <aside
                className={cn(
                    'fixed inset-y-0 left-0 z-[60] w-64 flex flex-col transition-transform duration-300 ease-in-out',
                    // Light: blue electric gradient | Dark: deep navy (marketed palette)
                    'bg-gradient-to-b from-[#0057e7] to-[#005e8a] dark:from-[#0D1B2A] dark:to-[#0f1f33]',
                    'md:translate-x-0 md:static md:z-auto md:shrink-0',
                    isOpen ? 'translate-x-0' : '-translate-x-full',
                )}
            >
                {/* Header — perfil del usuario */}
                <div className="px-4 py-4 border-b border-white/10 dark:border-[#1B263B] shrink-0">
                    <div className="flex items-center justify-between gap-2">
                        <Link
                            to="/perfil"
                            onClick={onClose}
                            className="flex items-center gap-3 min-w-0 flex-1 rounded-lg hover:bg-white/10 dark:hover:bg-[#1B263B] transition-colors duration-150 p-1 -m-1"
                        >
                            <UserAvatar
                                firstName={user?.firstName ?? '?'}
                                lastName={user?.lastName ?? '?'}
                                pictureURL={user?.profile.pictureURL}
                                size="md"
                            />
                            <div className="flex flex-col min-w-0">
                                <span className="text-sm font-semibold text-white leading-tight break-words">
                                    {user ? `${user.firstName} ${user.lastName}` : 'Usuario'}
                                </span>
                                <span className="text-xs text-white/60 truncate leading-tight mt-0.5">
                                    {user ? ROLE_LABELS[user.role] : ''}
                                </span>
                            </div>
                        </Link>

                        {/* Cerrar sidebar — solo mobile */}
                        <button
                            onClick={onClose}
                            aria-label="Cerrar menú"
                            className="md:hidden w-8 h-8 rounded-lg flex items-center justify-center text-white/70 hover:text-white hover:bg-white/10 dark:hover:bg-[#1B263B] transition-colors duration-150 shrink-0"
                        >
                            <X size={16} />
                        </button>
                    </div>
                </div>

                {/* Navigation */}
                <nav className="flex flex-col flex-1 px-3 py-4 gap-0.5 overflow-y-auto">
                    {/* Top nav items */}
                    {topNav.map((item) => {
                        if (item.kind === 'link') {
                            const Icon = item.icon
                            return (
                                <Link
                                    key={item.href}
                                    to={item.href}
                                    onClick={onClose}
                                    className={cn(
                                        navItemBase,
                                        isActive(item.href)
                                            ? 'bg-white/15 dark:bg-[#1B263B]'
                                            : 'hover:bg-white/10 dark:hover:bg-[#1B263B]',
                                    )}
                                >
                                    <Icon size={18} className="text-white/80 shrink-0" />
                                    {item.label}
                                </Link>
                            )
                        }

                        // Expandable item
                        const Icon = item.icon
                        return (
                            <div key={item.label}>
                                <button
                                    onClick={() => setIsOfficialExpanded((p) => !p)}
                                    className={cn(navItemBase, 'hover:bg-white/10 dark:hover:bg-[#1B263B] justify-between')}
                                >
                                    <span className="flex items-center gap-3">
                                        <Icon size={18} className="text-white/80 shrink-0" />
                                        {item.label}
                                    </span>
                                    <ChevronDown
                                        size={15}
                                        className={cn(
                                            'text-white/60 shrink-0 transition-transform duration-300',
                                            isOfficialExpanded && 'rotate-180',
                                        )}
                                    />
                                </button>

                                {/* Accordion children */}
                                <div
                                    className={cn(
                                        'overflow-hidden transition-[max-height] duration-300 ease-in-out',
                                        isOfficialExpanded ? 'max-h-48' : 'max-h-0',
                                    )}
                                >
                                    <div className="pl-9 pr-2 pb-1 flex flex-col gap-0.5 mt-0.5">
                                        {item.children.map((child) => (
                                            <a
                                                key={child.label}
                                                href={child.href}
                                                target="_blank"
                                                rel="noopener noreferrer"
                                                onClick={onClose}
                                                className="flex items-center justify-between gap-2 px-3 py-2 rounded-lg text-sm text-white/80 hover:text-white hover:bg-white/10 dark:hover:bg-[#1B263B] transition-all duration-150"
                                            >
                                                {child.label}
                                                <ExternalLink size={12} className="text-white/50 shrink-0" />
                                            </a>
                                        ))}
                                    </div>
                                </div>
                            </div>
                        )
                    })}

                    {/* Bottom section */}
                    <div className="mt-auto pt-4 border-t border-white/20 dark:border-[#1B263B] flex flex-col gap-0.5">
                        <Link
                            to="/contribuir"
                            onClick={onClose}
                            className={cn(
                                navItemBase,
                                isActive('/contribuir')
                                    ? 'bg-white/15 dark:bg-[#1B263B]'
                                    : 'hover:bg-white/10 dark:hover:bg-[#1B263B]',
                            )}
                        >
                            <GitFork size={18} className="text-white/80 shrink-0" />
                            Contribuír
                        </Link>

                        <button
                            onClick={handleLogout}
                            className={cn(
                                navItemBase,
                                'text-white/80 hover:bg-red-500/20 hover:text-red-200',
                            )}
                        >
                            <LogOut size={18} className="shrink-0" />
                            Cerrar Sesión
                        </button>

                        {/* Version footer */}
                        <p className="px-3 pt-3 pb-1 text-[10px] text-white/30 leading-relaxed select-none">
                            UTNet v0.1 (Beta)<br />
                            Por estudiantes, para estudiantes
                        </p>
                    </div>
                </nav>
            </aside>
        </>
    )
}
