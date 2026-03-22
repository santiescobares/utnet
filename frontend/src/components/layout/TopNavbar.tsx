import { useState } from 'react'
import { Bell, GraduationCap, Menu, Settings } from 'lucide-react'
import { SettingsModal } from '@/components/ui/SettingsModal'

interface TopNavbarProps {
    onMenuToggle: () => void
}

export function TopNavbar({ onMenuToggle }: TopNavbarProps) {
    const [settingsOpen, setSettingsOpen] = useState(false)

    return (
        <>
            <header className="h-16 shrink-0 flex items-center justify-between px-4 sm:px-6 border-b border-border/50 sticky top-0 z-50 bg-background">
                {/* Left: hamburger + logo */}
                <div className="flex items-center gap-3">
                    <button
                        onClick={onMenuToggle}
                        aria-label="Abrir menú"
                        className="md:hidden w-9 h-9 rounded-lg flex items-center justify-center text-foreground/70 hover:text-foreground hover:bg-secondary transition-colors duration-150"
                    >
                        <Menu size={20} />
                    </button>

                    <div className="flex items-center gap-2">
                        <div className="w-8 h-8 rounded-lg bg-primary/10 flex items-center justify-center">
                            <GraduationCap size={18} className="text-primary" />
                        </div>
                        <span className="font-bold text-lg tracking-tight select-none">UTNet</span>
                    </div>
                </div>

                {/* Right: notifications + settings */}
                <div className="flex items-center gap-1 sm:gap-2">
                    <button
                        aria-label="Notificaciones"
                        className="relative w-9 h-9 rounded-lg flex items-center justify-center text-foreground/70 hover:text-foreground hover:bg-secondary transition-colors duration-150"
                    >
                        <Bell size={18} />
                        {/* Badge placeholder */}
                        <span className="absolute top-1.5 right-1.5 w-2 h-2 rounded-full bg-primary" />
                    </button>

                    <button
                        onClick={() => setSettingsOpen(true)}
                        aria-label="Ajustes"
                        className="w-9 h-9 rounded-lg flex items-center justify-center text-foreground/70 hover:text-foreground hover:bg-secondary transition-colors duration-150"
                    >
                        <Settings size={18} />
                    </button>
                </div>
            </header>

            <SettingsModal open={settingsOpen} onClose={() => setSettingsOpen(false)} />
        </>
    )
}
