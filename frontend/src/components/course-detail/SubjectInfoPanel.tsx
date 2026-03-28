import { useEffect, useLayoutEffect, useMemo, useRef, useState } from 'react'
import { GraduationCap, Clock, Pencil, Plus, Trash2, Save, ChevronDown, Loader2 } from 'lucide-react'
import { toast } from 'sonner'
import { cn } from '@/lib/utils'
import { useAuthStore } from '@/store/authStore'
import type { CourseSubjectDTO, CourseSubjectUpdateDTO } from '@/types/course.types'
import type { SubjectDTO } from '@/types/subject.types'

// ── Traducciones ──────────────────────────────────────────────────────────────

const PROFESSOR_POSITION_LABELS: Record<string, string> = {
    FULL_PROFESSOR: 'Titular',
    ASSOCIATE_PROFESSOR: 'Asociado',
    ASSISTANT_PROFESSOR: 'Adjunto',
    TEACHING_INSTRUCTOR: 'JTP',
    GRADUATE_ASSISTANT: 'Ay. 1°',
    STUDENT_ASSISTANT: 'Ay. 2°',
}

const PROFESSOR_POSITION_ORDER = [
    'FULL_PROFESSOR',
    'ASSOCIATE_PROFESSOR',
    'ASSISTANT_PROFESSOR',
    'TEACHING_INSTRUCTOR',
    'GRADUATE_ASSISTANT',
    'STUDENT_ASSISTANT',
]

const DAY_LABELS: Record<string, string> = {
    MONDAY: 'Lunes',
    TUESDAY: 'Martes',
    WEDNESDAY: 'Miércoles',
    THURSDAY: 'Jueves',
    FRIDAY: 'Viernes',
    SATURDAY: 'Sábado',
    SUNDAY: 'Domingo',
}

const DAY_ORDER = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY']

// ── Custom select (dropdown fixed) ────────────────────────────────────────────

interface SelectOption { value: string; label: string }

interface CustomSelectProps {
    value: string
    onChange: (v: string) => void
    options: SelectOption[]
    placeholder?: string
    className?: string
}

function CustomSelect({ value, onChange, options, placeholder = 'Seleccionar', className }: CustomSelectProps) {
    const [open, setOpen] = useState(false)
    const [pos, setPos] = useState<{ top: number; left: number; width: number } | null>(null)
    const btnRef = useRef<HTMLButtonElement>(null)
    const dropRef = useRef<HTMLDivElement>(null)

    const handleOpen = () => {
        if (open) { setOpen(false); return }
        if (btnRef.current) {
            const r = btnRef.current.getBoundingClientRect()
            setPos({ top: r.bottom + 2, left: r.left, width: r.width })
        }
        setOpen(true)
    }

    useEffect(() => {
        if (!open) return
        const handler = (e: MouseEvent) => {
            if (
                !btnRef.current?.contains(e.target as Node) &&
                !dropRef.current?.contains(e.target as Node)
            ) setOpen(false)
        }
        document.addEventListener('mousedown', handler)
        return () => document.removeEventListener('mousedown', handler)
    }, [open])

    const selectedLabel = options.find((o) => o.value === value)?.label ?? ''

    return (
        <>
            <button
                ref={btnRef}
                type="button"
                onClick={handleOpen}
                className={cn(
                    'flex items-center justify-between gap-1 px-2.5 py-1.5 text-xs rounded-lg',
                    'border border-border bg-secondary text-foreground',
                    'hover:border-primary/40 transition-colors duration-150 touch-manipulation min-w-0',
                    className,
                )}
            >
                <span className="truncate">
                    {selectedLabel || <span className="text-muted-foreground">{placeholder}</span>}
                </span>
                <ChevronDown size={11} className={cn('shrink-0 transition-transform duration-150', open && 'rotate-180')} />
            </button>
            {open && pos && (
                <div
                    ref={dropRef}
                    style={{ top: pos.top, left: pos.left, width: pos.width }}
                    className="fixed z-50 bg-card border border-border rounded-xl shadow-lg py-1 animate-in fade-in zoom-in-95 duration-150 max-h-48 overflow-y-auto"
                >
                    {options.length === 0 ? (
                        <p className="px-3 py-2 text-xs text-muted-foreground">Sin opciones disponibles</p>
                    ) : (
                        options.map((opt) => (
                            <button
                                key={opt.value}
                                type="button"
                                onClick={() => { onChange(opt.value); setOpen(false) }}
                                className="w-full text-left px-3 py-2 text-xs text-foreground hover:bg-secondary transition-colors duration-100 touch-manipulation"
                            >
                                {opt.label}
                            </button>
                        ))
                    )}
                </div>
            )}
        </>
    )
}

// ── Time helpers ───────────────────────────────────────────────────────────────

function isEndValid(start: string, end: string): boolean {
    if (!start || !end) return true
    return end > start
}

function parseSchedule(s: string): [string, string] {
    const parts = s.split(/\s*[-–]\s*/)
    return [parts[0]?.trim() ?? '', parts[1]?.trim() ?? '']
}

function formatSchedule(start: string, end: string): string {
    if (start && end) return `${start} - ${end}`
    return start || end
}

// ── Props ──────────────────────────────────────────────────────────────────────

interface SubjectInfoPanelProps {
    courseSubject: CourseSubjectDTO | null
    subjectDetail: SubjectDTO | null
    accentColor?: string // preservado por compatibilidad
    onUpdate?: (id: number, dto: CourseSubjectUpdateDTO) => Promise<void>
    className?: string
}

export function SubjectInfoPanel({
    courseSubject,
    subjectDetail,
    onUpdate,
    className,
}: SubjectInfoPanelProps) {
    const user = useAuthStore((s) => s.user)
    const canEdit = user?.role === 'CONTRIBUTOR_3' || user?.role === 'ADMINISTRATOR'

    // ── Smooth fade+slide when subject changes (flicker-free) ────────────────
    const [visible, setVisible] = useState(true)
    const prevIdRef = useRef<number | null | undefined>(subjectDetail?.id)

    useLayoutEffect(() => {
        if (prevIdRef.current === subjectDetail?.id) return
        prevIdRef.current = subjectDetail?.id
        setVisible(false)
    }, [subjectDetail?.id])

    useEffect(() => {
        if (visible) return
        const t = setTimeout(() => setVisible(true), 40)
        return () => clearTimeout(t)
    }, [visible])

    // ── Edit state ────────────────────────────────────────────────────────────
    const [editMode, setEditMode] = useState(false)
    const [editProfessors, setEditProfessors] = useState<[string, string][]>([])
    const [editClassDaysTimes, setEditClassDaysTimes] = useState<[string, string, string][]>([]) // [day, start, end]
    const [saving, setSaving] = useState(false)

    const enterEditMode = () => {
        if (!courseSubject) return
        setEditProfessors(Object.entries(courseSubject.professors))
        setEditClassDaysTimes(
            Object.entries(courseSubject.classDays)
                .sort(([a], [b]) => DAY_ORDER.indexOf(a) - DAY_ORDER.indexOf(b))
                .map(([day, schedule]) => {
                    const [start, end] = parseSchedule(schedule)
                    return [day, start, end] as [string, string, string]
                })
        )
        setEditMode(true)
    }

    const handleSave = async () => {
        if (!courseSubject || !onUpdate) return
        const hasInvalidTimes = editClassDaysTimes.some(([, start, end]) => !isEndValid(start, end))
        if (hasInvalidTimes) {
            toast.error('La hora de fin debe ser posterior a la de inicio.')
            return
        }
        setSaving(true)
        try {
            const professors = Object.fromEntries(
                editProfessors
                    .filter(([p, n]) => p && n.trim())
                    .sort(([a], [b]) => PROFESSOR_POSITION_ORDER.indexOf(a) - PROFESSOR_POSITION_ORDER.indexOf(b))
            )
            const classDays = Object.fromEntries(
                editClassDaysTimes
                    .filter(([d]) => d)
                    .sort(([a], [b]) => DAY_ORDER.indexOf(a) - DAY_ORDER.indexOf(b))
                    .map(([day, start, end]) => [day, formatSchedule(start, end)])
            )
            await onUpdate(courseSubject.id, { professors, classDays })
            setEditMode(false)
        } catch {
            toast.error('No se pudo guardar la información.')
        } finally {
            setSaving(false)
        }
    }

    // Detecta si hay cambios respecto al estado original
    const hasChanges = useMemo(() => {
        if (!courseSubject) return false

        const origProfs = Object.entries(courseSubject.professors)
            .sort(([a], [b]) => PROFESSOR_POSITION_ORDER.indexOf(a) - PROFESSOR_POSITION_ORDER.indexOf(b))
        const editProfs = editProfessors
            .filter(([p, n]) => p && n.trim())
            .sort(([a], [b]) => PROFESSOR_POSITION_ORDER.indexOf(a) - PROFESSOR_POSITION_ORDER.indexOf(b))
        if (JSON.stringify(origProfs) !== JSON.stringify(editProfs)) return true

        const origDays = Object.entries(courseSubject.classDays)
            .sort(([a], [b]) => DAY_ORDER.indexOf(a) - DAY_ORDER.indexOf(b))
            .map(([day, schedule]) => { const [s, e] = parseSchedule(schedule); return [day, s, e] })
        const editDays = [...editClassDaysTimes]
            .filter(([d]) => d)
            .sort(([a], [b]) => DAY_ORDER.indexOf(a) - DAY_ORDER.indexOf(b))
        if (JSON.stringify(origDays) !== JSON.stringify(editDays)) return true

        return false
    }, [courseSubject, editProfessors, editClassDaysTimes])

    // Opciones disponibles (excluye las ya usadas en otras filas)
    const usedPositions = editProfessors.map(([p]) => p).filter(Boolean)
    const usedDays = editClassDaysTimes.map(([d]) => d).filter(Boolean)

    const availablePositions = (current: string): SelectOption[] =>
        PROFESSOR_POSITION_ORDER
            .filter((p) => p === current || !usedPositions.includes(p))
            .map((p) => ({ value: p, label: PROFESSOR_POSITION_LABELS[p] ?? p }))

    const availableDays = (current: string): SelectOption[] =>
        DAY_ORDER
            .filter((d) => d === current || !usedDays.includes(d))
            .map((d) => ({ value: d, label: DAY_LABELS[d] ?? d }))

    const animStyle = {
        opacity: visible ? 1 : 0,
        transform: visible ? 'none' : 'translateY(6px)',
        transition: 'opacity 0.3s cubic-bezier(0.4,0,0.2,1), transform 0.3s cubic-bezier(0.4,0,0.2,1)',
    }

    if (!courseSubject || !subjectDetail) {
        return (
            <div
                className={cn('flex flex-col items-center justify-center gap-2 py-8 text-center px-4', className)}
                style={animStyle}
            >
                <GraduationCap size={24} className="text-muted-foreground/40" />
                <p className="text-sm text-muted-foreground">Seleccioná una materia</p>
            </div>
        )
    }

    const professors = Object.entries(courseSubject.professors)
    const classDays = Object.entries(courseSubject.classDays).sort(
        ([a], [b]) => DAY_ORDER.indexOf(a) - DAY_ORDER.indexOf(b),
    )

    return (
        <div className={cn('flex flex-col gap-4 px-4 py-5', className)} style={animStyle}>

            {/* Subject name + edit button */}
            <div className="flex items-start justify-between gap-2">
                <div className="min-w-0">
                    <h2 className="text-lg font-bold text-foreground leading-tight">
                        {subjectDetail.name}
                    </h2>
                    {subjectDetail.shortName && subjectDetail.shortName !== subjectDetail.name && (
                        <span className="text-xs text-muted-foreground font-medium mt-0.5 block">
                            {subjectDetail.shortName}
                        </span>
                    )}
                </div>
                {canEdit && !editMode && (
                    <button
                        onClick={enterEditMode}
                        className="min-w-[32px] min-h-[32px] flex items-center justify-center rounded-lg text-muted-foreground hover:text-foreground hover:bg-secondary transition-colors duration-150 shrink-0 touch-manipulation mt-0.5"
                        title="Editar información"
                    >
                        <Pencil size={13} />
                    </button>
                )}
            </div>

            {/* ── VIEW MODE ─────────────────────────────────────────────────── */}
            {!editMode && (
                <>
                    {professors.length > 0 && (
                        <div className="flex flex-col gap-1.5">
                            <div className="flex items-center gap-1.5">
                                <GraduationCap size={13} className="text-muted-foreground shrink-0" />
                                <span className="text-xs font-semibold text-muted-foreground uppercase tracking-wide">
                                    Docentes
                                </span>
                            </div>
                            <div className="flex flex-col gap-1">
                                {professors.map(([position, name]) => (
                                    <div key={position} className="flex items-baseline gap-2">
                                        <span className="text-[11px] text-muted-foreground min-w-[4rem] shrink-0">
                                            {PROFESSOR_POSITION_LABELS[position] ?? position}
                                        </span>
                                        <span className="text-sm text-foreground font-medium leading-tight">
                                            {name}
                                        </span>
                                    </div>
                                ))}
                            </div>
                        </div>
                    )}

                    {classDays.length > 0 && (
                        <div className="flex flex-col gap-1.5">
                            <div className="flex items-center gap-1.5">
                                <Clock size={13} className="text-muted-foreground shrink-0" />
                                <span className="text-xs font-semibold text-muted-foreground uppercase tracking-wide">
                                    Horarios
                                </span>
                            </div>
                            <div className="flex flex-col gap-1">
                                {classDays.map(([day, schedule]) => (
                                    <div key={day} className="flex items-baseline gap-2">
                                        <span className="text-[11px] text-muted-foreground min-w-[4rem] shrink-0">
                                            {DAY_LABELS[day] ?? day}
                                        </span>
                                        <span className="text-sm text-foreground leading-tight">
                                            {schedule}
                                        </span>
                                    </div>
                                ))}
                            </div>
                        </div>
                    )}
                </>
            )}

            {/* ── EDIT MODE ─────────────────────────────────────────────────── */}
            {editMode && (
                <div className="flex flex-col gap-4">

                    {/* Docentes */}
                    <div className="flex flex-col gap-2">
                        <div className="flex items-center gap-1.5">
                            <GraduationCap size={13} className="text-muted-foreground shrink-0" />
                            <span className="text-xs font-semibold text-muted-foreground uppercase tracking-wide">
                                Docentes
                            </span>
                            <button
                                type="button"
                                onClick={() => setEditProfessors((prev) => [...prev, ['', '']])}
                                disabled={editProfessors.length >= PROFESSOR_POSITION_ORDER.length}
                                className="min-w-[20px] min-h-[20px] flex items-center justify-center rounded-md text-muted-foreground hover:text-foreground hover:bg-secondary transition-colors duration-150 disabled:opacity-30 touch-manipulation"
                                title="Agregar docente"
                            >
                                <Plus size={12} />
                            </button>
                        </div>
                        <div className="flex flex-col gap-2">
                            {editProfessors.length === 0 && (
                                <p className="text-xs text-muted-foreground italic">
                                    Sin docentes. Presioná + para agregar.
                                </p>
                            )}
                            {editProfessors.map(([position, name], idx) => (
                                <div key={idx} className="flex items-center gap-1.5">
                                    <CustomSelect
                                        value={position}
                                        onChange={(v) =>
                                            setEditProfessors((prev) =>
                                                prev.map((row, i) => i === idx ? [v, row[1]] : row)
                                            )
                                        }
                                        options={availablePositions(position)}
                                        placeholder="Cargo"
                                        className="w-[6.5rem] shrink-0"
                                    />
                                    <input
                                        type="text"
                                        value={name}
                                        onChange={(e) =>
                                            setEditProfessors((prev) =>
                                                prev.map((row, i) => i === idx ? [row[0], e.target.value] : row)
                                            )
                                        }
                                        placeholder="Nombre y apellido"
                                        className={cn(
                                            'flex-1 min-w-0 px-2.5 py-1.5 text-xs rounded-lg',
                                            'border border-border bg-secondary text-foreground',
                                            'focus:outline-none focus:ring-2 focus:ring-primary/30 focus:border-primary/50',
                                            'transition-all duration-150 placeholder:text-muted-foreground',
                                        )}
                                    />
                                    <button
                                        type="button"
                                        onClick={() => setEditProfessors((prev) => prev.filter((_, i) => i !== idx))}
                                        className="min-w-[24px] min-h-[24px] flex items-center justify-center rounded-lg text-muted-foreground hover:text-destructive hover:bg-destructive/10 transition-colors duration-150 shrink-0 touch-manipulation"
                                    >
                                        <Trash2 size={11} />
                                    </button>
                                </div>
                            ))}
                        </div>
                    </div>

                    {/* Horarios */}
                    <div className="flex flex-col gap-2">
                        <div className="flex items-center gap-1.5">
                            <Clock size={13} className="text-muted-foreground shrink-0" />
                            <span className="text-xs font-semibold text-muted-foreground uppercase tracking-wide">
                                Horarios
                            </span>
                            <button
                                type="button"
                                onClick={() => setEditClassDaysTimes((prev) => [...prev, ['', '', '']])}
                                disabled={editClassDaysTimes.length >= DAY_ORDER.length}
                                className="min-w-[20px] min-h-[20px] flex items-center justify-center rounded-md text-muted-foreground hover:text-foreground hover:bg-secondary transition-colors duration-150 disabled:opacity-30 touch-manipulation"
                                title="Agregar horario"
                            >
                                <Plus size={12} />
                            </button>
                        </div>
                        <div className="flex flex-col gap-2">
                            {editClassDaysTimes.length === 0 && (
                                <p className="text-xs text-muted-foreground italic">
                                    Sin horarios. Presioná + para agregar.
                                </p>
                            )}
                            {editClassDaysTimes.map(([day, start, end], idx) => (
                                <div key={idx} className="flex items-center gap-1.5">
                                    <CustomSelect
                                        value={day}
                                        onChange={(v) =>
                                            setEditClassDaysTimes((prev) =>
                                                prev.map((row, i) => i === idx ? [v, row[1], row[2]] : row)
                                            )
                                        }
                                        options={availableDays(day)}
                                        placeholder="Día"
                                        className="w-[6.5rem] shrink-0"
                                    />
                                    <input
                                        type="time"
                                        value={start}
                                        onChange={(e) =>
                                            setEditClassDaysTimes((prev) =>
                                                prev.map((row, i) => i === idx ? [row[0], e.target.value, row[2]] : row)
                                            )
                                        }
                                        className={cn(
                                            'flex-1 min-w-0 px-2.5 py-1.5 text-xs rounded-lg',
                                            'border border-border bg-secondary text-foreground',
                                            'focus:outline-none focus:ring-2 focus:ring-primary/30 focus:border-primary/50',
                                            'transition-all duration-150',
                                        )}
                                    />
                                    <span className="text-xs text-muted-foreground shrink-0">–</span>
                                    <input
                                        type="time"
                                        value={end}
                                        onChange={(e) =>
                                            setEditClassDaysTimes((prev) =>
                                                prev.map((row, i) => i === idx ? [row[0], row[1], e.target.value] : row)
                                            )
                                        }
                                        className={cn(
                                            'flex-1 min-w-0 px-2.5 py-1.5 text-xs rounded-lg',
                                            'border bg-secondary text-foreground',
                                            'focus:outline-none focus:ring-2',
                                            'transition-all duration-150',
                                            isEndValid(start, end)
                                                ? 'border-border focus:ring-primary/30 focus:border-primary/50'
                                                : 'border-destructive/60 focus:ring-destructive/30',
                                        )}
                                    />
                                    <button
                                        type="button"
                                        onClick={() => setEditClassDaysTimes((prev) => prev.filter((_, i) => i !== idx))}
                                        className="min-w-[24px] min-h-[24px] flex items-center justify-center rounded-lg text-muted-foreground hover:text-destructive hover:bg-destructive/10 transition-colors duration-150 shrink-0 touch-manipulation"
                                    >
                                        <Trash2 size={11} />
                                    </button>
                                </div>
                            ))}
                        </div>
                    </div>

                    {/* Save / Cancel */}
                    <div className="flex items-center justify-end gap-2 pt-1">
                        <button
                            type="button"
                            onClick={() => setEditMode(false)}
                            disabled={saving}
                            className="px-3 py-1.5 text-xs font-medium rounded-xl border border-border bg-card text-foreground hover:bg-secondary transition-colors duration-150 disabled:opacity-50 touch-manipulation"
                        >
                            Cancelar
                        </button>
                        <button
                            type="button"
                            onClick={handleSave}
                            disabled={saving || !hasChanges}
                            className={cn(
                                'flex items-center gap-1.5 px-3 py-1.5 text-xs font-medium rounded-xl',
                                'bg-primary text-primary-foreground hover:bg-primary/90',
                                'transition-all duration-150 disabled:opacity-50 touch-manipulation',
                            )}
                        >
                            {saving
                                ? <Loader2 size={11} className="animate-spin" />
                                : <Save size={11} />
                            }
                            Guardar
                        </button>
                    </div>
                </div>
            )}
        </div>
    )
}
