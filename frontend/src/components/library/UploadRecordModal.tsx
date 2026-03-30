import { useEffect, useRef, useState } from 'react'
import { Loader2, Upload, X } from 'lucide-react'
import { toast } from 'sonner'
import { cn } from '@/lib/utils'
import { subjectService } from '@/services/subject.service'
import { studyRecordService } from '@/services/studyRecord.service'
import type { SubjectDTO } from '@/types/subject.types'
import type { StudyRecordType } from '@/types/studyrecord.types'

const ALLOWED_EXTENSIONS = ['pdf', 'doc', 'docx', 'png', 'jpg', 'jpeg']
const MAX_FILE_SIZE_MB = 50

interface UploadRecordModalProps {
    open: boolean
    onClose: () => void
    onSuccess?: () => void
}

export function UploadRecordModal({ open, onClose, onSuccess }: UploadRecordModalProps) {
    const [title, setTitle] = useState('')
    const [description, setDescription] = useState('')
    const [subjectId, setSubjectId] = useState<number | ''>('')
    const [tagsInput, setTagsInput] = useState('')
    const [file, setFile] = useState<File | null>(null)
    const [fileError, setFileError] = useState('')
    const [isSubmitting, setIsSubmitting] = useState(false)
    const [recordType, setRecordType] = useState<StudyRecordType>('NOTE')
    const [subjects, setSubjects] = useState<SubjectDTO[]>([])

    const titleRef = useRef<HTMLInputElement>(null)
    const fileInputRef = useRef<HTMLInputElement>(null)

    // Reset on open
    useEffect(() => {
        if (open) {
            setTitle('')
            setDescription('')
            setSubjectId('')
            setRecordType('NOTE')
            setTagsInput('')
            setFile(null)
            setFileError('')
            setTimeout(() => titleRef.current?.focus(), 50)
        }
    }, [open])

    // Load subjects once
    useEffect(() => {
        if (!open || subjects.length > 0) return
        subjectService.getAll().then(setSubjects).catch(() => {})
    }, [open]) // eslint-disable-line react-hooks/exhaustive-deps

    // Escape key
    useEffect(() => {
        if (!open) return
        const handler = (e: KeyboardEvent) => { if (e.key === 'Escape') onClose() }
        document.addEventListener('keydown', handler)
        return () => document.removeEventListener('keydown', handler)
    }, [open, onClose])

    // Body scroll lock
    useEffect(() => {
        if (open) document.body.style.overflow = 'hidden'
        else document.body.style.overflow = ''
        return () => { document.body.style.overflow = '' }
    }, [open])

    const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const selected = e.target.files?.[0] ?? null
        setFileError('')
        if (!selected) { setFile(null); return }

        const ext = selected.name.split('.').pop()?.toLowerCase() ?? ''
        if (!ALLOWED_EXTENSIONS.includes(ext)) {
            setFileError(`Formato no permitido. Usá: ${ALLOWED_EXTENSIONS.join(', ')}`)
            setFile(null)
            return
        }
        if (selected.size > MAX_FILE_SIZE_MB * 1024 * 1024) {
            setFileError(`El archivo supera el límite de ${MAX_FILE_SIZE_MB} MB`)
            setFile(null)
            return
        }
        setFile(selected)
    }

    const parsedTags = tagsInput
        .split(',')
        .map((t) => t.trim().toLowerCase())
        .filter((t) => t.length > 0 && !t.includes(' '))

    const isValid = title.trim().length >= 5 && description.trim().length >= 1 && subjectId !== '' && file !== null


    const handleSubmit = async () => {
        if (!isValid || isSubmitting) return
        setIsSubmitting(true)
        try {
            await studyRecordService.create(
                {
                    subjectIds: [subjectId as number],
                    title: title.trim(),
                    description: description.trim(),
                    type: recordType,
                    tags: parsedTags.length > 0 ? parsedTags : undefined,
                },
                file!,
            )
            toast.success('Aporte publicado correctamente')
            onSuccess?.()
            onClose()
        } catch {
            toast.error('No se pudo publicar el aporte. Intentá de nuevo.')
        } finally {
            setIsSubmitting(false)
        }
    }

    if (!open) return null

    return (
        <div className="fixed inset-0 z-50 flex items-end sm:items-center justify-center">
            {/* Backdrop */}
            <div
                className="absolute inset-0 bg-black/50 backdrop-blur-sm animate-in fade-in duration-200"
                onClick={onClose}
            />

            {/* Panel */}
            <div className={cn(
                'relative z-10 w-full sm:max-w-lg',
                'bg-card border border-border',
                'rounded-t-2xl sm:rounded-2xl shadow-2xl',
                'flex flex-col max-h-[90dvh]',
                'animate-in fade-in slide-in-from-bottom-4 sm:zoom-in-95 duration-200',
            )}>
                {/* Header */}
                <div className="flex items-center gap-3 px-5 py-4 border-b border-border shrink-0">
                    <div className="w-9 h-9 rounded-xl bg-primary/10 flex items-center justify-center shrink-0">
                        <Upload size={18} className="text-primary" />
                    </div>
                    <div className="flex-1 min-w-0">
                        <h2 className="font-semibold text-base text-foreground">Subir aporte</h2>
                        <p className="text-xs text-muted-foreground mt-0.5">Compartí material con la comunidad</p>
                    </div>
                    <button
                        onClick={onClose}
                        disabled={isSubmitting}
                        className="p-1.5 rounded-lg text-muted-foreground hover:text-foreground hover:bg-secondary transition-colors duration-150 disabled:opacity-40"
                    >
                        <X size={16} />
                    </button>
                </div>

                {/* Body */}
                <div className="flex-1 overflow-y-auto px-5 py-4 flex flex-col gap-4">

                    {/* Título */}
                    <div className="flex flex-col gap-1.5">
                        <label className="text-sm font-medium text-foreground">
                            Título <span className="text-destructive">*</span>
                        </label>
                        <input
                            ref={titleRef}
                            type="text"
                            value={title}
                            maxLength={100}
                            onChange={(e) => setTitle(e.target.value)}
                            placeholder="Ej: Resumen Análisis Matemático I"
                            className={cn(
                                'w-full px-3 py-2.5 text-sm rounded-xl border bg-secondary border-border text-foreground',
                                'focus:outline-none focus:ring-2 focus:ring-primary/30 focus:border-primary/50',
                                'transition-all duration-150 placeholder:text-muted-foreground',
                            )}
                        />
                        <span className="text-[11px] text-muted-foreground text-right">{title.length}/100</span>
                    </div>

                    {/* Descripción */}
                    <div className="flex flex-col gap-1.5">
                        <label className="text-sm font-medium text-foreground">
                            Descripción <span className="text-destructive">*</span>
                        </label>
                        <textarea
                            value={description}
                            onChange={(e) => setDescription(e.target.value)}
                            maxLength={2000}
                            rows={3}
                            placeholder="Describí brevemente el contenido del material..."
                            className={cn(
                                'w-full px-3 py-2.5 text-sm rounded-xl resize-none',
                                'bg-secondary border border-border text-foreground',
                                'focus:outline-none focus:ring-2 focus:ring-primary/30 focus:border-primary/50',
                                'transition-all duration-150 placeholder:text-muted-foreground',
                            )}
                        />
                        <span className="text-[11px] text-muted-foreground text-right">{description.length}/2000</span>
                    </div>

                    {/* Materia */}
                    <div className="flex flex-col gap-1.5">
                        <label className="text-sm font-medium text-foreground">
                            Materia <span className="text-destructive">*</span>
                        </label>
                        <select
                            value={subjectId}
                            onChange={(e) => setSubjectId(e.target.value ? Number(e.target.value) : '')}
                            className={cn(
                                'w-full px-3 py-2.5 text-sm rounded-xl border bg-secondary border-border text-foreground',
                                'focus:outline-none focus:ring-2 focus:ring-primary/30 focus:border-primary/50',
                                'transition-all duration-150',
                                subjectId === '' && 'text-muted-foreground',
                            )}
                        >
                            <option value="">Seleccioná una materia...</option>
                            {subjects.map((s) => (
                                <option key={s.id} value={s.id}>{s.name}</option>
                            ))}
                        </select>
                    </div>

                    {/* Tags */}
                    <div className="flex flex-col gap-1.5">
                        <label className="text-sm font-medium text-foreground">
                            Tags <span className="text-muted-foreground font-normal">(opcional)</span>
                        </label>
                        <input
                            type="text"
                            value={tagsInput}
                            onChange={(e) => setTagsInput(e.target.value)}
                            placeholder="Ej: calculo, integrales, parcial"
                            className={cn(
                                'w-full px-3 py-2.5 text-sm rounded-xl border bg-secondary border-border text-foreground',
                                'focus:outline-none focus:ring-2 focus:ring-primary/30 focus:border-primary/50',
                                'transition-all duration-150 placeholder:text-muted-foreground',
                            )}
                        />
                        <p className="text-[11px] text-muted-foreground">Separados por coma, sin espacios dentro de cada tag.</p>
                    </div>

                    {/* Archivo */}
                    <div className="flex flex-col gap-1.5">
                        <label className="text-sm font-medium text-foreground">
                            Archivo <span className="text-destructive">*</span>
                        </label>
                        <button
                            type="button"
                            onClick={() => fileInputRef.current?.click()}
                            className={cn(
                                'w-full flex flex-col items-center gap-2 px-4 py-5 rounded-xl border-2 border-dashed',
                                'transition-all duration-150 text-center',
                                file
                                    ? 'border-primary/50 bg-primary/5 text-primary'
                                    : 'border-border bg-secondary text-muted-foreground hover:border-primary/40 hover:bg-primary/5',
                            )}
                        >
                            <Upload size={20} className={file ? 'text-primary' : 'text-muted-foreground'} />
                            {file ? (
                                <span className="text-sm font-medium text-foreground">{file.name}</span>
                            ) : (
                                <>
                                    <span className="text-sm font-medium">Hacé click para seleccionar</span>
                                    <span className="text-xs">{ALLOWED_EXTENSIONS.join(', ')} · Máx {MAX_FILE_SIZE_MB} MB</span>
                                </>
                            )}
                        </button>
                        {fileError && (
                            <p className="text-xs text-destructive">{fileError}</p>
                        )}
                        <input
                            ref={fileInputRef}
                            type="file"
                            accept={ALLOWED_EXTENSIONS.map((e) => `.${e}`).join(',')}
                            onChange={handleFileChange}
                            className="hidden"
                        />
                    </div>
                </div>

                {/* Footer */}
                <div className="flex items-center justify-end gap-2 px-5 py-4 border-t border-border shrink-0">
                    <button
                        onClick={onClose}
                        disabled={isSubmitting}
                        className="px-4 py-2.5 text-sm font-medium rounded-xl border border-border bg-card text-foreground hover:bg-secondary transition-colors duration-150 disabled:opacity-40"
                    >
                        Cancelar
                    </button>
                    <button
                        onClick={handleSubmit}
                        disabled={!isValid || isSubmitting}
                        className={cn(
                            'px-4 py-2.5 text-sm font-medium rounded-xl flex items-center gap-2',
                            'bg-primary text-primary-foreground hover:bg-primary/90',
                            'transition-all duration-150 disabled:opacity-40 disabled:cursor-not-allowed',
                        )}
                    >
                        {isSubmitting && <Loader2 size={14} className="animate-spin" />}
                        Publicar
                    </button>
                </div>
            </div>
        </div>
    )
}
