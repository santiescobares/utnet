import { useEffect, useLayoutEffect, useRef, useState } from 'react'
import { ChevronLeft, ChevronRight } from 'lucide-react'
import { cn } from '@/lib/utils'
import type { CourseEventDTO } from '@/types/course.types'

const MONTH_NAMES = [
    'Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio',
    'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre',
]
const DAY_LABELS = ['DOM', 'LUN', 'MAR', 'MIÉ', 'JUE', 'VIE', 'SÁB']

function groupEventsByDate(events: CourseEventDTO[]): Map<string, CourseEventDTO[]> {
    const map = new Map<string, CourseEventDTO[]>()
    for (const event of events) {
        const existing = map.get(event.date) ?? []
        map.set(event.date, [...existing, event])
    }
    return map
}

export interface CalendarGridProps {
    year: number
    month: number               // 0-indexed
    events: CourseEventDTO[]
    selectedEventId: number | null
    selectedDate?: string | null
    accentColor?: string        // hex sin '#'
    canGoPrev: boolean
    canGoNext: boolean
    onPrevMonth: () => void
    onNextMonth: () => void
    onEventSelect: (eventId: number) => void
    onDayClick?: (dateStr: string) => void
    onOverflowDayClick?: (dateStr: string) => void
    className?: string
}

export function CalendarGrid({
    year,
    month,
    events,
    selectedEventId,
    selectedDate,
    accentColor,
    canGoPrev,
    canGoNext,
    onPrevMonth,
    onNextMonth,
    onEventSelect,
    onDayClick,
    onOverflowDayClick,
    className,
}: CalendarGridProps) {
    const accent = accentColor ? `#${accentColor}` : undefined
    const todayStr = new Date().toISOString().split('T')[0]

    // ── Smooth month transition ───────────────────────────────────────────────
    // useLayoutEffect sets opacity=0 synchronously before paint so the new
    // month's cells never flash at full opacity.
    const [visible, setVisible] = useState(true)
    const monthKeyRef = useRef(`${year}-${month}`)
    const currentMonthKey = `${year}-${month}`

    useLayoutEffect(() => {
        if (monthKeyRef.current === currentMonthKey) return
        monthKeyRef.current = currentMonthKey
        setVisible(false)
    }, [currentMonthKey])

    useEffect(() => {
        if (visible) return
        const t = setTimeout(() => setVisible(true), 30)
        return () => clearTimeout(t)
    }, [visible])

    // ── Build weeks (7-cell rows) ─────────────────────────────────────────────
    const firstDay = new Date(year, month, 1).getDay()
    const daysInMonth = new Date(year, month + 1, 0).getDate()
    const prevYear = month === 0 ? year - 1 : year
    const prevMonth = month === 0 ? 11 : month - 1
    const prevMonthDays = new Date(prevYear, prevMonth + 1, 0).getDate()
    const nextYear = month === 11 ? year + 1 : year
    const nextMonth = month === 11 ? 0 : month + 1

    const pad = (n: number) => String(n).padStart(2, '0')
    type Cell = { day: number; dateStr: string; overflow: boolean }
    const cells: Cell[] = [
        ...Array.from({ length: firstDay }, (_, i) => {
            const d = prevMonthDays - firstDay + 1 + i
            return { day: d, dateStr: `${prevYear}-${pad(prevMonth + 1)}-${pad(d)}`, overflow: true }
        }),
        ...Array.from({ length: daysInMonth }, (_, i) => {
            const d = i + 1
            return { day: d, dateStr: `${year}-${pad(month + 1)}-${pad(d)}`, overflow: false }
        }),
    ]
    let nextDay = 1
    while (cells.length % 7 !== 0) {
        cells.push({ day: nextDay, dateStr: `${nextYear}-${pad(nextMonth + 1)}-${pad(nextDay)}`, overflow: true })
        nextDay++
    }
    const weeks: Cell[][] = []
    for (let i = 0; i < cells.length; i += 7) weeks.push(cells.slice(i, i + 7))

    const eventsByDate = groupEventsByDate(events)

    return (
        <div className={cn('flex flex-col select-none h-full', className)}>

            {/* ── Month navigation header (static — no transition) ── */}
            <div className="flex items-center justify-between mb-2 shrink-0">
                <button
                    onClick={onPrevMonth}
                    disabled={!canGoPrev}
                    className={cn(
                        'min-w-[36px] min-h-[36px] flex items-center justify-center rounded-xl',
                        'text-muted-foreground hover:text-foreground hover:bg-secondary',
                        'transition-colors duration-150 disabled:opacity-25 disabled:cursor-not-allowed touch-manipulation',
                    )}
                    aria-label="Mes anterior"
                >
                    <ChevronLeft size={16} />
                </button>

                <span className="text-sm font-semibold text-foreground tracking-tight">
                    {MONTH_NAMES[month]} {year}
                </span>

                <button
                    onClick={onNextMonth}
                    disabled={!canGoNext}
                    className={cn(
                        'min-w-[36px] min-h-[36px] flex items-center justify-center rounded-xl',
                        'text-muted-foreground hover:text-foreground hover:bg-secondary',
                        'transition-colors duration-150 disabled:opacity-25 disabled:cursor-not-allowed touch-manipulation',
                    )}
                    aria-label="Mes siguiente"
                >
                    <ChevronRight size={16} />
                </button>
            </div>

            {/* ── Day labels (static — no transition) ── */}
            <div className="grid grid-cols-7 mb-1 shrink-0">
                {DAY_LABELS.map((d) => (
                    <div
                        key={d}
                        className="text-center text-[10px] font-semibold tracking-widest text-muted-foreground/60 py-1 uppercase"
                    >
                        {d}
                    </div>
                ))}
            </div>

            {/* ── Flexbox weeks — each row gets equal height ── */}
            <div
                className="flex flex-col flex-1 min-h-0"
                style={{
                    opacity: visible ? 1 : 0,
                    transform: visible ? 'translateY(0)' : 'translateY(8px)',
                    transition: 'opacity 0.3s cubic-bezier(0.4,0,0.2,1), transform 0.3s cubic-bezier(0.4,0,0.2,1)',
                }}
            >
                {weeks.map((week, wi) => (
                    <div key={wi} className="flex flex-1 min-h-0 overflow-hidden">
                        {week.map((cell, di) => {
                            if (cell === null) return <div key={`e-${wi}-${di}`} className="flex-1" />

                            const { day, dateStr, overflow } = cell
                            const dayEvents = eventsByDate.get(dateStr) ?? []
                            const isToday = dateStr === todayStr
                            const hasSelected = dayEvents.some((e) => e.id === selectedEventId)
                            const isSelectedDay = selectedDate === dateStr
                            const visibleEvents = dayEvents.slice(0, 2)
                            const extraCount = dayEvents.length - 2
                            const dimmed = overflow

                            return (
                                <div
                                    key={dateStr}
                                    onClick={() => overflow ? onOverflowDayClick?.(dateStr) : onDayClick?.(dateStr)}
                                    className={cn(
                                        'flex-1 pt-1 pb-0.5 flex flex-col items-center gap-0.5',
                                        'transition-colors duration-150 rounded-lg',
                                        (overflow ? onOverflowDayClick : onDayClick) && 'cursor-pointer',
                                        hasSelected && 'bg-primary/[0.05]',
                                        isSelectedDay && !hasSelected && 'bg-secondary/80',
                                        !hasSelected && !isSelectedDay && 'hover:bg-secondary/50',
                                    )}
                                >
                                    {/* Day number */}
                                    <span
                                        style={isToday && accent
                                            ? { backgroundColor: accent, color: '#fff' }
                                            : undefined
                                        }
                                        className={cn(
                                            'w-6 h-6 flex items-center justify-center rounded-full',
                                            'text-[12px] font-medium leading-none transition-all duration-150',
                                            isToday && !accent && 'bg-primary text-primary-foreground',
                                            !isToday && dimmed && 'text-muted-foreground/50',
                                            !isToday && !dimmed && 'text-foreground',
                                        )}
                                    >
                                        {day}
                                    </span>

                                    {/* Event indicators */}
                                    <div className="flex flex-col gap-[2px] w-[70%]">
                                        {visibleEvents.map((event) => {
                                            const isSel = event.id === selectedEventId
                                            const dotColor = event.tagColor ? `#${event.tagColor}` : accent
                                            return (
                                                <button
                                                    key={event.id}
                                                    onClick={(e) => { e.stopPropagation(); onEventSelect(event.id) }}
                                                    title={event.description}
                                                    aria-label={`Evento: ${event.description}`}
                                                    style={dotColor
                                                        ? { backgroundColor: isSel ? dotColor : `${dotColor}90` }
                                                        : undefined
                                                    }
                                                    className={cn(
                                                        'w-full h-[3px] rounded-full touch-manipulation',
                                                        'transition-all duration-150 active:scale-95',
                                                        !dotColor && (isSel
                                                            ? 'bg-primary'
                                                            : 'bg-primary/50 hover:bg-primary/80'),
                                                    )}
                                                />
                                            )
                                        })}
                                    </div>

                                    {extraCount > 0 && (
                                        <span className="text-[9px] font-medium text-muted-foreground leading-none">
                                            +{extraCount}
                                        </span>
                                    )}
                                </div>
                            )
                        })}
                    </div>
                ))}
            </div>
        </div>
    )
}
