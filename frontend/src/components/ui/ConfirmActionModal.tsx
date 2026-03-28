import { useEffect, useRef } from 'react'
import { AlertTriangle, Loader2, X } from 'lucide-react'
import { cn } from '@/lib/utils'

export interface ConfirmActionModalProps {
    open: boolean
    onClose: () => void
    onConfirm: () => void
    title: string
    description: string
    confirmLabel: string
    confirmVariant?: 'default' | 'destructive'
    isLoading?: boolean
}

export function ConfirmActionModal({
    open,
    onClose,
    onConfirm,
    title,
    description,
    confirmLabel,
    confirmVariant = 'default',
    isLoading = false,
}: ConfirmActionModalProps) {
    const confirmRef = useRef<HTMLButtonElement>(null)

    // Focus confirm button when opened
    useEffect(() => {
        if (open) confirmRef.current?.focus()
    }, [open])

    // Close on Escape
    useEffect(() => {
        if (!open) return
        const handler = (e: KeyboardEvent) => { if (e.key === 'Escape') onClose() }
        document.addEventListener('keydown', handler)
        return () => document.removeEventListener('keydown', handler)
    }, [open, onClose])

    // Lock body scroll
    useEffect(() => {
        if (open) document.body.style.overflow = 'hidden'
        else document.body.style.overflow = ''
        return () => { document.body.style.overflow = '' }
    }, [open])

    if (!open) return null

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
            {/* Backdrop */}
            <div
                className="absolute inset-0 bg-black/50 backdrop-blur-sm animate-in fade-in duration-200"
                onClick={onClose}
            />

            {/* Panel */}
            <div className={cn(
                'relative z-10 w-full max-w-md',
                'bg-card border border-border rounded-2xl shadow-2xl',
                'p-6 flex flex-col gap-5',
                'animate-in fade-in zoom-in-95 duration-200',
            )}>
                {/* Close button */}
                <button
                    onClick={onClose}
                    disabled={isLoading}
                    className="absolute top-4 right-4 p-1.5 rounded-lg text-muted-foreground hover:text-foreground hover:bg-secondary transition-colors duration-150 disabled:opacity-40"
                >
                    <X size={16} />
                </button>

                {/* Header */}
                <div className="flex items-start gap-3 pr-6">
                    <div className="shrink-0 mt-0.5 w-9 h-9 rounded-xl bg-amber-100 dark:bg-amber-900/30 flex items-center justify-center">
                        <AlertTriangle size={18} className="text-amber-600 dark:text-amber-400" />
                    </div>
                    <div>
                        <h3 className="font-semibold text-base text-foreground leading-snug">{title}</h3>
                        <p className="text-sm text-muted-foreground mt-1 leading-relaxed">{description}</p>
                    </div>
                </div>

                {/* Warning */}
                <p className="text-xs text-muted-foreground bg-secondary rounded-xl px-4 py-3 leading-relaxed border border-border">
                    Recordá que las acciones malintencionadas son sancionables de acuerdo al reglamento institucional.
                </p>

                {/* Actions */}
                <div className="flex items-center justify-end gap-2">
                    <button
                        onClick={onClose}
                        disabled={isLoading}
                        className="px-4 py-2.5 text-sm font-medium rounded-xl border border-border bg-card text-foreground hover:bg-secondary transition-colors duration-150 disabled:opacity-40"
                    >
                        Cancelar
                    </button>
                    <button
                        ref={confirmRef}
                        onClick={onConfirm}
                        disabled={isLoading}
                        className={cn(
                            'px-4 py-2.5 text-sm font-medium rounded-xl flex items-center gap-2',
                            'transition-all duration-150 disabled:opacity-60 disabled:cursor-not-allowed',
                            confirmVariant === 'destructive'
                                ? 'bg-destructive text-destructive-foreground hover:bg-destructive/90'
                                : 'bg-primary text-primary-foreground hover:bg-primary/90',
                        )}
                    >
                        {isLoading && <Loader2 size={14} className="animate-spin" />}
                        {confirmLabel}
                    </button>
                </div>
            </div>
        </div>
    )
}
