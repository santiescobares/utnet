import { useState } from 'react'
import { useNavigate } from 'react-router'
import { Toaster } from 'sonner'
import { LoginModal } from '@/components/auth/LoginModal'
import { useAuthStore } from '@/store/authStore'
import { GraduationCap, BookOpen, Users, Star } from 'lucide-react'

export function LandingPage() {
    const [loginOpen, setLoginOpen] = useState(false)
    const { isAuthenticated } = useAuthStore()
    const navigate = useNavigate()

    const handleIngresar = () => {
        if (isAuthenticated) {
            navigate('/home')
        } else {
            setLoginOpen(true)
        }
    }

    return (
        <>
            <Toaster position="bottom-center" richColors />
            <LoginModal open={loginOpen} onClose={() => setLoginOpen(false)} />

            <div className="min-h-screen bg-background text-foreground flex flex-col">
                {/* Navbar */}
                <header className="flex items-center justify-between px-6 py-4 border-b border-border/50 backdrop-blur-sm sticky top-0 z-40 bg-background/80">
                    <div className="flex items-center gap-2">
                        <div className="w-8 h-8 rounded-lg bg-primary/10 flex items-center justify-center">
                            <GraduationCap size={18} className="text-primary" />
                        </div>
                        <span className="font-bold text-lg tracking-tight">UTNet</span>
                    </div>
                    <button
                        onClick={handleIngresar}
                        className="px-5 py-2 rounded-xl text-sm font-semibold bg-primary text-primary-foreground hover:bg-primary/90 active:scale-[0.97] transition-all duration-200 shadow-sm hover:shadow-md hover:shadow-primary/20"
                    >
                        Ingresar
                    </button>
                </header>

                {/* Hero */}
                <main className="flex-1 flex flex-col items-center justify-center px-6 py-20 text-center gap-8">
                    <div className="flex flex-col items-center gap-4 max-w-lg">
                        <div className="w-16 h-16 rounded-2xl bg-primary/10 flex items-center justify-center mb-2">
                            <GraduationCap size={32} className="text-primary" />
                        </div>
                        <h1 className="text-4xl sm:text-5xl font-extrabold tracking-tight leading-tight">
                            La red de la{' '}
                            <span className="text-primary">comunidad universitaria</span>
                        </h1>
                        <p className="text-foreground/60 text-base sm:text-lg leading-relaxed">
                            Foros, apuntes, reseñas de profesores y mucho más. Todo en un solo lugar, para vos.
                        </p>
                        <button
                            onClick={handleIngresar}
                            className="mt-2 px-8 py-3.5 rounded-xl text-sm font-semibold bg-primary text-primary-foreground hover:bg-primary/90 active:scale-[0.97] transition-all duration-200 shadow-lg hover:shadow-xl hover:shadow-primary/25"
                        >
                            Empezar ahora
                        </button>
                    </div>

                    {/* Feature pills */}
                    <div className="flex flex-wrap items-center justify-center gap-3 mt-4">
                        {[
                            { icon: BookOpen, label: 'Apuntes y recursos' },
                            { icon: Users, label: 'Foros de discusión' },
                            { icon: Star, label: 'Reseñas de profesores' },
                        ].map(({ icon: Icon, label }) => (
                            <div
                                key={label}
                                className="flex items-center gap-2 px-4 py-2 rounded-full bg-secondary/60 border border-border/50 text-sm text-foreground/70"
                            >
                                <Icon size={14} className="text-primary" />
                                <span>{label}</span>
                            </div>
                        ))}
                    </div>
                </main>
            </div>
        </>
    )
}
