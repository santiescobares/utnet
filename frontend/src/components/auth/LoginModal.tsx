import { useEffect, useRef, useState } from 'react';
import { GoogleLogin } from '@react-oauth/google';
import { X, Loader2, ArrowLeft, ArrowRight, GraduationCap, UserRoundPlus, Calendar, User } from 'lucide-react';
import { toast } from 'sonner';
import { authService } from '@/services/auth.service';
import { userService } from '@/services/user.service';
import { useAuthStore } from '@/store/authStore';
import { cn } from '@/lib/utils';

// ── Types ────────────────────────────────────────────────────────────────────

type ModalStep = 'login' | 'register';

interface RegisterForm {
    firstName: string;
    lastName: string;
    birthday: string;
}

interface RegisterErrors {
    firstName?: string;
    lastName?: string;
    birthday?: string;
}

interface LoginModalProps {
    open: boolean;
    onClose: () => void;
}

// ── JWT payload decoder (sin verificación — solo para UX de autocompletado) ──

function decodeJwtPayload<T>(token: string): T | null {
    try {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const json = decodeURIComponent(
            atob(base64)
                .split('')
                .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
                .join('')
        );
        return JSON.parse(json) as T;
    } catch {
        return null;
    }
}

interface RegistrationTokenPayload {
    firstName?: string;
    lastName?: string;
    email?: string;
}

// ── Validation ───────────────────────────────────────────────────────────────

function validateRegisterForm(form: RegisterForm): RegisterErrors {
    const errors: RegisterErrors = {};

    if (!form.firstName.trim()) {
        errors.firstName = 'El nombre es obligatorio';
    } else if (form.firstName.trim().length < 3) {
        errors.firstName = 'Mínimo 3 caracteres';
    }

    if (!form.lastName.trim()) {
        errors.lastName = 'El apellido es obligatorio';
    } else if (form.lastName.trim().length < 3) {
        errors.lastName = 'Mínimo 3 caracteres';
    }

    if (!form.birthday) {
        errors.birthday = 'La fecha de nacimiento es obligatoria';
    } else {
        const date = new Date(form.birthday);
        const today = new Date();
        today.setHours(0, 0, 0, 0);

        const ageLimitDate = new Date();
        ageLimitDate.setFullYear(ageLimitDate.getFullYear() - 16);
        ageLimitDate.setHours(0, 0, 0, 0);

        if (date > today) {
            errors.birthday = 'Debe ser una fecha pasada';
        } else if (date > ageLimitDate) {
            errors.birthday = 'No se permiten menores de 16 años';
        }
    }

    return errors;
}

// ── Input Component ──────────────────────────────────────────────────────────

interface InputFieldProps {
    id: string;
    label: string;
    type?: string;
    value: string;
    placeholder?: string;
    error?: string;
    icon: React.ReactNode;
    max?: string;
    hint?: string;
    onChange: (value: string) => void;
    onBlur?: () => void;
}

function InputField({ id, label, type = 'text', value, placeholder, error, icon, max, hint, onChange, onBlur }: InputFieldProps) {
    return (
        <div className="flex flex-col gap-1.5">
            <label htmlFor={id} className="text-sm font-medium text-foreground/80">
                {label}
            </label>
            <div className="relative">
                <span className="absolute left-3.5 top-1/2 -translate-y-1/2 text-foreground/40 pointer-events-none">
                    {icon}
                </span>
                <input
                    id={id}
                    type={type}
                    value={value}
                    placeholder={placeholder}
                    max={max}
                    onChange={(e) => onChange(e.target.value)}
                    onBlur={onBlur}
                    className={cn(
                        'w-full pl-10 pr-4 py-3 rounded-xl text-sm',
                        'bg-secondary/60 border transition-all duration-200 outline-none',
                        'placeholder:text-foreground/30 text-foreground',
                        'focus:ring-2 focus:ring-primary/40 focus:border-primary/60',
                        error
                            ? 'border-destructive/60 focus:ring-destructive/30 focus:border-destructive/60'
                            : 'border-border/50 hover:border-border'
                    )}
                />
            </div>
            {error ? (
                <p className="text-xs text-destructive font-medium animate-in fade-in slide-in-from-top-1 duration-200">
                    {error}
                </p>
            ) : hint ? (
                <p className="text-xs text-foreground/40">{hint}</p>
            ) : null}
        </div>
    );
}

// ── Main Component ───────────────────────────────────────────────────────────

export function LoginModal({ open, onClose }: LoginModalProps) {
    const { setUser, setRegistrationToken, registrationToken } = useAuthStore();

    const [step, setStep] = useState<ModalStep>('login');
    const [isLoading, setIsLoading] = useState(false);
    const [isVisible, setIsVisible] = useState(false);
    const overlayRef = useRef<HTMLDivElement>(null);

    const [form, setForm] = useState<RegisterForm>({ firstName: '', lastName: '', birthday: '' });
    const [errors, setErrors] = useState<RegisterErrors>({});
    const [touched, setTouched] = useState<Record<keyof RegisterForm, boolean>>({
        firstName: false,
        lastName: false,
        birthday: false,
    });

    // Animación de entrada/salida
    useEffect(() => {
        if (open) setIsVisible(true);
    }, [open]);

    // Cerrar con Escape
    useEffect(() => {
        if (!open) return;
        const handleKeyDown = (e: KeyboardEvent) => {
            if (e.key === 'Escape') handleClose();
        };
        window.addEventListener('keydown', handleKeyDown);
        return () => window.removeEventListener('keydown', handleKeyDown);
    }, [open]);

    // Bloquear scroll
    useEffect(() => {
        document.body.style.overflow = open ? 'hidden' : '';
        return () => { document.body.style.overflow = ''; };
    }, [open]);

    const handleClose = () => {
        setIsVisible(false);
        setTimeout(() => {
            onClose();
            setStep('login');
            setForm({ firstName: '', lastName: '', birthday: '' });
            setErrors({});
            setTouched({ firstName: false, lastName: false, birthday: false });
        }, 250);
    };

    const handleOverlayClick = (e: React.MouseEvent<HTMLDivElement>) => {
        if (e.target === overlayRef.current) handleClose();
    };

    // Google OAuth — credential es el id_token JWT que el backend espera
    const handleGoogleSuccess = async (credential: string) => {
        setIsLoading(true);
        try {
            const result = await authService.login({ googleIdToken: credential });

            if (result.user) {
                setUser(result.user);
                toast.success(`Se inició sesión como ${result.user.firstName} ${result.user.lastName}.`);
                handleClose();
            } else if (result.registrationToken) {
                setRegistrationToken(result.registrationToken);

                // Autocompletar nombre/apellido decodificando el JWT del backend
                const payload = decodeJwtPayload<RegistrationTokenPayload>(result.registrationToken);
                if (payload) {
                    setForm({
                        firstName: payload.firstName ?? '',
                        lastName: payload.lastName ?? '',
                        birthday: '',
                    });
                }

                setStep('register');
            }
        } catch (error: unknown) {
            const message = (error as { response?: { data?: { message?: string } } })?.response?.data?.message;
            toast.error(message ?? 'Error al iniciar sesión. Intentá de nuevo.');
        } finally {
            setIsLoading(false);
        }
    };

    const updateField = (field: keyof RegisterForm, value: string) => {
        let v = value;
        // Solo letras y espacios, máximo 20 caracteres
        if (field === 'firstName' || field === 'lastName') {
            v = v.replace(/[^a-zA-ZáéíóúÁÉÍÓÚñÑ\s]/g, '');
            if (v.length > 20) {
                v = v.slice(0, 20);
            }
        }

        setForm((prev) => ({ ...prev, [field]: v }));
        if (touched[field]) {
            const newForm = { ...form, [field]: v };
            const newErrors = validateRegisterForm(newForm);
            setErrors((prev) => ({ ...prev, [field]: newErrors[field] }));
        }
    };

    const handleBlur = (field: keyof RegisterForm) => {
        setTouched((prev) => ({ ...prev, [field]: true }));
        const newErrors = validateRegisterForm(form);
        setErrors((prev) => ({ ...prev, [field]: newErrors[field] }));
    };

    const handleRegister = async (e: React.FormEvent) => {
        e.preventDefault();
        setTouched({ firstName: true, lastName: true, birthday: true });
        const newErrors = validateRegisterForm(form);
        setErrors(newErrors);
        if (Object.keys(newErrors).length > 0) return;
        if (!registrationToken) return;

        setIsLoading(true);
        try {
            const user = await userService.createUser({
                registrationToken,
                firstName: form.firstName.trim(),
                lastName: form.lastName.trim(),
                birthday: form.birthday,
            });
            setUser(user);
            toast.success(`Se creó tu cuenta de UTNet.`);
            handleClose();
        } catch (error: unknown) {
            const message = (error as { response?: { data?: { message?: string } } })?.response?.data?.message;
            toast.error(message ?? 'Error al crear la cuenta. Intentá de nuevo.');
        } finally {
            setIsLoading(false);
        }
    };

    const handleBackToLogin = () => {
        setStep('login');
        setForm({ firstName: '', lastName: '', birthday: '' });
        setErrors({});
        setTouched({ firstName: false, lastName: false, birthday: false });
    };

    if (!open && !isVisible) return null;

    const maxDate = new Date();
    maxDate.setFullYear(maxDate.getFullYear() - 16);
    const maxDateStr = maxDate.toISOString().split('T')[0];

    return (
        <div
            ref={overlayRef}
            onClick={handleOverlayClick}
            className={cn(
                'fixed inset-0 z-50 flex items-center justify-center p-4',
                'bg-black/50 backdrop-blur-sm',
                'transition-opacity duration-250',
                isVisible && open ? 'opacity-100' : 'opacity-0 pointer-events-none'
            )}
        >
            {/* Modal */}
            <div
                className={cn(
                    'relative w-full max-w-sm bg-card text-card-foreground',
                    'rounded-2xl shadow-2xl border border-border/50',
                    'transition-all duration-250',
                    isVisible && open
                        ? 'opacity-100 scale-100 translate-y-0'
                        : 'opacity-0 scale-95 translate-y-4'
                )}
            >
                {/* Cerrar */}
                <button
                    onClick={handleClose}
                    aria-label="Cerrar"
                    className={cn(
                        'absolute top-4 right-4 z-10',
                        'w-8 h-8 rounded-full flex items-center justify-center',
                        'text-foreground/50 hover:text-foreground',
                        'bg-secondary/60 hover:bg-secondary',
                        'transition-all duration-200 hover:scale-110'
                    )}
                >
                    <X size={16} strokeWidth={2.5} />
                </button>

                {/* ── STEP: LOGIN ── */}
                {step === 'login' && (
                    <div className="p-8 flex flex-col items-center gap-6">
                        {/* Header */}
                        <div className="flex flex-col items-center gap-2 text-center">
                            <div className="w-12 h-12 rounded-2xl bg-primary/10 flex items-center justify-center mb-1">
                                <GraduationCap size={24} className="text-primary" />
                            </div>
                            <h2 className="text-xl font-bold tracking-tight">Ingresá a UTNet</h2>
                            <p className="text-sm text-foreground/55 leading-relaxed max-w-[300px]">
                                Accedé con tu cuenta de Google para una autenticación rápida y segura. Sin contraseñas.
                            </p>
                        </div>

                        {/* Divisor */}
                        <div className="w-full h-px bg-border/50" />

                        {/* Google Button */}
                        <div className="w-full flex flex-col items-center gap-3">
                            {isLoading ? (
                                <button
                                    disabled
                                    className="w-full flex items-center justify-center gap-3 py-3.5 px-5 rounded-xl text-sm font-semibold bg-background border border-border opacity-60 cursor-not-allowed"
                                >
                                    <Loader2 size={18} className="animate-spin text-foreground/60" />
                                    <span>Verificando...</span>
                                </button>
                            ) : (
                                <GoogleLogin
                                    onSuccess={(credentialResponse) => {
                                        if (credentialResponse.credential) {
                                            handleGoogleSuccess(credentialResponse.credential);
                                        }
                                    }}
                                    onError={() => toast.error('Error al conectar con Google. Intentá de nuevo.')}
                                    useOneTap={false}
                                    text="continue_with"
                                    shape="rectangular"
                                    width="320"
                                    theme="outline"
                                />
                            )}
                        </div>

                        {/* Footer */}
                        <p className="text-xs text-foreground/40 text-center">
                            Al continuar, aceptás nuestros{' '}
                            <span className="text-primary/70 cursor-pointer hover:text-primary transition-colors">
                                Términos y Condiciones de Uso
                            </span>{' '}
                            y{' '}
                            <span className="text-primary/70 cursor-pointer hover:text-primary transition-colors">
                                Políticas de Privacidad
                            </span>
                        </p>
                    </div>
                )}

                {/* ── STEP: REGISTER ── */}
                {step === 'register' && (
                    <div className="p-8 flex flex-col items-center gap-6">
                        {/* Header — mismo estilo que login step */}
                        <div className="flex flex-col items-center gap-2 text-center w-full">
                            <button
                                onClick={handleBackToLogin}
                                className="self-start flex items-center gap-1.5 text-xs text-foreground/50 hover:text-foreground transition-colors -ml-1 mb-2"
                            >
                                <ArrowLeft size={14} />
                                <span>Volver</span>
                            </button>
                            <div className="w-12 h-12 rounded-2xl bg-primary/10 flex items-center justify-center mb-1">
                                <UserRoundPlus size={22} className="text-primary" />
                            </div>
                            <h2 className="text-xl font-bold tracking-tight">Completá tus datos</h2>
                            <p className="text-sm text-foreground/55 leading-relaxed max-w-[320px]">
                                UTNet es un entorno universitario. Es importante que puedas identificarte públicamente.
                            </p>
                        </div>

                        {/* Divisor */}
                        <div className="w-full h-px bg-border/50" />

                        {/* Formulario */}
                        <form onSubmit={handleRegister} className="flex flex-col gap-4 w-full" noValidate>
                            <InputField
                                id="register-firstName"
                                label="Nombre"
                                value={form.firstName}
                                placeholder="Juan Cruz"
                                error={touched.firstName ? errors.firstName : undefined}
                                icon={<User size={16} />}
                                onChange={(v) => updateField('firstName', v)}
                                onBlur={() => handleBlur('firstName')}
                            />
                            <InputField
                                id="register-lastName"
                                label="Apellido"
                                value={form.lastName}
                                placeholder="Pérez"
                                error={touched.lastName ? errors.lastName : undefined}
                                icon={<User size={16} />}
                                onChange={(v) => updateField('lastName', v)}
                                onBlur={() => handleBlur('lastName')}
                            />
                            <InputField
                                id="register-birthday"
                                label="Fecha de Nacimiento"
                                type="date"
                                value={form.birthday}
                                error={touched.birthday ? errors.birthday : undefined}
                                icon={<Calendar size={16} />}
                                max={maxDateStr}
                                onChange={(v) => updateField('birthday', v)}
                                onBlur={() => handleBlur('birthday')}
                            />

                            {/* Submit */}
                            <button
                                type="submit"
                                disabled={isLoading}
                                className={cn(
                                    'w-full py-3.5 rounded-xl text-sm font-semibold mt-1 group',
                                    'bg-primary text-primary-foreground',
                                    'hover:bg-primary/90 active:scale-[0.98]',
                                    'transition-all duration-300',
                                    'shadow-[0_4px_14px_0_hsl(var(--primary)/0.3)] hover:shadow-[0_6px_20px_hsl(var(--primary)/0.4)] hover:-translate-y-0.5',
                                    'disabled:opacity-60 disabled:cursor-not-allowed disabled:active:scale-100 disabled:hover:translate-y-0 disabled:hover:shadow-[0_4px_14px_0_hsl(var(--primary)/0.3)]',
                                    'flex items-center justify-center gap-2'
                                )}
                            >
                                {isLoading ? (
                                    <>
                                        <Loader2 size={16} className="animate-spin" />
                                        <span>Creando cuenta...</span>
                                    </>
                                ) : (
                                    <>
                                        <span>Crear cuenta</span>
                                        <ArrowRight size={16} className="transition-transform duration-300 group-hover:translate-x-1" />
                                    </>
                                )}
                            </button>
                        </form>
                    </div>
                )}
            </div>
        </div>
    );
}
