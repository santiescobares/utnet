import { cn } from '@/lib/utils'
import type { CourseEventDTO } from '@/types/course.types'

const DAY_LABELS = ['Dom', 'Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb']
const WEEKEND_COLS = new Set([0, 6]) // Dom, Sáb

function groupEventsByDate(events: CourseEventDTO[]): Map<string, CourseEventDTO[]> {
    const map = new Map<string, CourseEventDTO[]>()
    for (const event of events) {
        const existing = map.get(event.date) ?? []
        map.set(event.date, [...existing, event])
    }
    return map
}

interface CalendarSectionProps {
    year: number
    month: number               // 0-indexed
    events: CourseEventDTO[]
    selectedEventId: number | null
    accentColor?: string        // hex sin '#'
    onEventSelect: (eventId: number) => void
}

export function CalendarSection({
    year,
    month,
    events,
    selectedEventId,
    accentColor,
    onEventSelect,
}: CalendarSectionProps) {
    const accent = accentColor ? `#${accentColor}` : undefined
    const todayStr = new Date().toISOString().split('T')[0]

    const firstDay = new Date(year, month, 1).getDay()
    const daysInMonth = new Date(year, month + 1, 0).getDate()

    const cells: (number | null)[] = [
        ...Array.from({ length: firstDay }, () => null),
        ...Array.from({ length: daysInMonth }, (_, i) => i + 1),
    ]
    while (cells.length % 7 !== 0) cells.push(null)

    const weeks = Array.from({ length: cells.length / 7 }, (_, i) => cells.slice(i * 7, i * 7 + 7))

    const eventsByDate = groupEventsByDate(events)

    return (
        <div className="flex flex-col animate-in fade-in duration-300">

            {/* Day headers */}
            <div className="grid grid-cols-7 mb-1">
                {DAY_LABELS.map((d, i) => (
                    <div
                        key={d}
                        className={cn(
                            'text-center text-[11px] font-semibold py-2 select-none',
                            WEEKEND_COLS.has(i) ? 'text-muted-foreground/40' : 'text-muted-foreground/70',
                        )}
                    >
                        {d}
                    </div>
                ))}
            </div>

            {/* Weeks */}
            <div className="flex flex-col divide-y divide-border/30">
                {weeks.map((week, wi) => (
                    <div key={wi} className="grid grid-cols-7">
                        {week.map((day, di) => {
                            const colIdx = di // 0=Dom … 6=Sáb
                            if (day === null) {
                                return (
                                    <div
                                        key={`empty-${wi}-${di}`}
                                        className={cn(
                                            'min-h-[60px] sm:min-h-[68px]',
                                            WEEKEND_COLS.has(colIdx) && 'bg-muted/20',
                                        )}
                                    />
                                )
                            }

                            const dateStr = `${year}-${String(month + 1).padStart(2, '0')}-${String(day).padStart(2, '0')}`
                            const dayEvents = eventsByDate.get(dateStr) ?? []
                            const isToday = dateStr === todayStr
                            const hasSelected = dayEvents.some((e) => e.id === selectedEventId)
                            const visibleEvents = dayEvents.slice(0, 2)
                            const extraCount = dayEvents.length - 2

                            return (
                                <div
                                    key={dateStr}
                                    className={cn(
                                        'min-h-[60px] sm:min-h-[68px] p-1 sm:p-1.5 flex flex-col gap-0.5',
                                        'transition-colors duration-150',
                                        WEEKEND_COLS.has(colIdx) && 'bg-muted/20',
                                        hasSelected && 'bg-primary/[0.06]',
                                        dayEvents.length > 0 && !hasSelected && 'hover:bg-accent/5 cursor-pointer',
                                    )}
                                >
                                    {/* Day number */}
                                    <span
                                        style={isToday && accent
                                            ? { backgroundColor: accent, color: '#fff', boxShadow: `0 1px 6px ${accent}55` }
                                            : undefined
                                        }
                                        className={cn(
                                            'self-center w-7 h-7 flex items-center justify-center rounded-full mb-0.5',
                                            'text-[12px] font-medium select-none transition-colors duration-150',
                                            isToday && !accent && 'bg-primary text-primary-foreground shadow-sm',
                                            !isToday && WEEKEND_COLS.has(colIdx) && 'text-foreground/50',
                                            !isToday && !WEEKEND_COLS.has(colIdx) && 'text-foreground',
                                        )}
                                    >
                                        {day}
                                    </span>

                                    {/* Event bars */}
                                    {visibleEvents.map((event) => {
                                        const isSelected = event.id === selectedEventId
                                        const barColor = event.tagColor ? `#${event.tagColor}` : accent
                                        return (
                                            <button
                                                key={event.id}
                                                onClick={() => onEventSelect(event.id)}
                                                title={event.description}
                                                aria-label={`Evento: ${event.description}`}
                                                style={barColor
                                                    ? {
                                                        backgroundColor: isSelected ? barColor : `${barColor}80`,
                                                        ...(isSelected ? { boxShadow: `0 0 0 1.5px ${barColor}` } : {}),
                                                    }
                                                    : undefined
                                                }
                                                className={cn(
                                                    'w-full h-2 rounded-md touch-manipulation',
                                                    'transition-all duration-150 active:scale-95',
                                                    !barColor && (isSelected
                                                        ? 'bg-primary ring-1 ring-primary ring-offset-1'
                                                        : 'bg-primary/50 hover:bg-primary/70'),
                                                    'animate-in fade-in duration-200',
                                                )}
                                            />
                                        )
                                    })}

                                    {extraCount > 0 && (
                                        <span className="text-[10px] font-medium text-muted-foreground text-center leading-none mt-0.5 select-none">
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
