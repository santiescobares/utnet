import { useEffect, useState } from 'react'
import { useTheme } from 'next-themes'
import { Monitor, Moon, Sun, X } from 'lucide-react'
import { cn } from '@/lib/utils'

interface SettingsModalProps {
    open: boolean
    onClose: () => void
}

type ThemeOption = 'light' | 'dark' | 'system'

const themeOptions: { value: ThemeOption; label: string; icon: React.ElementType }[] = [
    { value: 'light', label: 'Claro', icon: Sun },
    { value: 'dark', label: 'Oscuro', icon: Moon },
    { value: 'system', label: 'Sistema', icon: Monitor },
]

function Toggle({
    checked,
    onChange,
    id,
}: {
    checked: boolean
    onChange: (v: boolean) => void
    id: string
}) {
    return (
        <button
            id={id}
            role="switch"
            aria-checked={checked}
            onClick={() => onChange(!checked)}
            className={cn(
                'relative inline-flex w-11 h-6 rounded-full transition-colors duration-200 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-primary/50 shrink-0',
                checked ? 'bg-primary' : 'bg-border',
            )}
        >
            <span
                className={cn(
                    'absolute top-0.5 left-0.5 w-5 h-5 rounded-full bg-white shadow-sm transition-transform duration-200',
                    checked ? 'translate-x-5' : 'translate-x-0',
                )}
            />
        </button>
    )
}

export function SettingsModal({ open, onClose }: SettingsModalProps) {
    const { theme, setTheme } = useTheme()

    // Mock preferences — local state until backend preferences are ready
    const [emailNotifications, setEmailNotifications] = useState(true)
    const [compactSidebar, setCompactSidebar] = useState(false)

    // Close on Escape
    useEffect(() => {
        if (!open) return
        const handler = (e: KeyboardEvent) => {
            if (e.key === 'Escape') onClose()
        }
        window.addEventListener('keydown', handler)
        return () => window.removeEventListener('keydown', handler)
    }, [open, onClose])

    // Lock body scroll while open
    useEffect(() => {
        if (open) {
            document.body.style.overflow = 'hidden'
        } else {
            document.body.style.overflow = ''
        }
        return () => {
            document.body.style.overflow = ''
        }
    }, [open])

    if (!open) return null

    return (
        <div
            className="fixed inset-0 z-[100] bg-black/50 flex items-center justify-center p-4"
            onClick={onClose}
        >
            <div
                className="bg-background border border-border rounded-2xl shadow-2xl w-full max-w-md animate-in fade-in zoom-in-95 duration-200"
                onClick={(e) => e.stopPropagation()}
            >
                {/* Header */}
                <div className="flex items-center justify-between px-6 py-5 border-b border-border/60">
                    <h2 className="text-base font-semibold text-foreground tracking-tight">Ajustes</h2>
                    <button
                        onClick={onClose}
                        aria-label="Cerrar ajustes"
                        className="w-8 h-8 rounded-lg flex items-center justify-center text-muted-foreground hover:text-foreground hover:bg-secondary transition-colors duration-150"
                    >
                        <X size={16} />
                    </button>
                </div>

                <div className="px-6 py-5 flex flex-col gap-6">
                    {/* Apariencia */}
                    <section className="flex flex-col gap-3">
                        <h3 className="text-xs font-semibold text-muted-foreground uppercase tracking-wider">
                            Apariencia
                        </h3>
                        <div className="flex gap-2">
                            {themeOptions.map(({ value, label, icon: Icon }) => (
                                <button
                                    key={value}
                                    onClick={() => setTheme(value)}
                                    className={cn(
                                        'flex-1 flex flex-col items-center gap-2 py-3 px-2 rounded-xl border-2 text-xs font-medium transition-all duration-150',
                                        theme === value
                                            ? 'border-primary bg-primary/10 text-primary'
                                            : 'border-border text-muted-foreground hover:border-border/60 hover:bg-secondary',
                                    )}
                                >
                                    <Icon size={18} />
                                    {label}
                                </button>
                            ))}
                        </div>
                    </section>

                    {/* Preferencias */}
                    <section className="flex flex-col gap-1">
                        <h3 className="text-xs font-semibold text-muted-foreground uppercase tracking-wider mb-2">
                            Preferencias
                        </h3>

                        {/* Notificaciones por correo */}
                        <div className="flex items-center justify-between py-3 border-b border-border/40">
                            <div className="flex flex-col gap-0.5 min-w-0 pr-4">
                                <label
                                    htmlFor="toggle-email"
                                    className="text-sm font-medium text-foreground cursor-pointer"
                                >
                                    Notificaciones por correo
                                </label>
                                <span className="text-xs text-muted-foreground">
                                    Recibí avisos y novedades en tu email
                                </span>
                            </div>
                            <Toggle
                                id="toggle-email"
                                checked={emailNotifications}
                                onChange={setEmailNotifications}
                            />
                        </div>

                        {/* Modo compacto */}
                        <div className="flex items-center justify-between py-3 border-b border-border/40">
                            <div className="flex flex-col gap-0.5 min-w-0 pr-4">
                                <label
                                    htmlFor="toggle-compact"
                                    className="text-sm font-medium text-foreground cursor-pointer"
                                >
                                    Modo compacto en sidebar
                                </label>
                                <span className="text-xs text-muted-foreground">
                                    Reducí el espaciado de los ítems de navegación
                                </span>
                            </div>
                            <Toggle
                                id="toggle-compact"
                                checked={compactSidebar}
                                onChange={setCompactSidebar}
                            />
                        </div>

                        {/* Idioma — placeholder */}
                        <div className="flex items-center justify-between py-3 opacity-50 cursor-not-allowed">
                            <div className="flex flex-col gap-0.5 min-w-0 pr-4">
                                <span className="text-sm font-medium text-foreground">Idioma</span>
                                <span className="text-xs text-muted-foreground">Próximamente</span>
                            </div>
                            <select
                                disabled
                                defaultValue="es"
                                className="text-sm bg-secondary border border-border rounded-lg px-3 py-1.5 text-foreground cursor-not-allowed"
                            >
                                <option value="es">Español</option>
                            </select>
                        </div>
                    </section>
                </div>
            </div>
        </div>
    )
}
