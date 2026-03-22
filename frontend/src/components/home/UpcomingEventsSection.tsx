import { MapPin } from 'lucide-react'
import { cn } from '@/lib/utils'
import { SectionHeader } from '@/components/ui/SectionHeader'
import type { EventType, UpcomingEvent } from '@/types/content.types'

interface UpcomingEventsSectionProps {
    events: UpcomingEvent[]
}

const MONTHS_ES = ['ene', 'feb', 'mar', 'abr', 'may', 'jun', 'jul', 'ago', 'sep', 'oct', 'nov', 'dic']

function formatEventDate(isoDate: string): { day: string; month: string } {
    const [, monthStr, dayStr] = isoDate.split('-')
    return {
        day: String(parseInt(dayStr, 10)),
        month: MONTHS_ES[parseInt(monthStr, 10) - 1] ?? '',
    }
}

const typeLabelMap: Record<EventType, string> = {
    presencial: 'Presencial',
    virtual: 'Virtual',
    hibrido: 'Híbrido',
}

const typeBadgeClass: Record<EventType, string> = {
    presencial: 'bg-emerald-100 text-emerald-700 dark:bg-emerald-900/30 dark:text-emerald-400',
    virtual: 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400',
    hibrido: 'bg-amber-100 text-amber-700 dark:bg-amber-900/30 dark:text-amber-400',
}

export function UpcomingEventsSection({ events }: UpcomingEventsSectionProps) {
    return (
        <section className="animate-in fade-in slide-in-from-bottom-2 duration-300">
            <SectionHeader title="Próximos eventos" />
            <div className="flex flex-col gap-3">
                {events.map((event) => {
                    const { day, month } = formatEventDate(event.date)
                    return (
                        <div
                            key={event.id}
                            className="flex gap-4 p-4 rounded-xl border border-border bg-card hover:border-primary/20 hover:shadow-sm transition-all duration-200"
                        >
                            {/* Date badge */}
                            <div className="flex flex-col items-center justify-center w-12 shrink-0 rounded-lg bg-primary/10 py-2 px-1">
                                <span className="text-xl font-bold text-primary leading-none">{day}</span>
                                <span className="text-[10px] text-primary/70 uppercase font-semibold mt-0.5 tracking-wide">{month}</span>
                            </div>

                            {/* Content */}
                            <div className="flex flex-col gap-1 min-w-0 flex-1">
                                <div className="flex items-start gap-2 flex-wrap">
                                    <h3 className="font-semibold text-sm text-foreground leading-snug flex-1 min-w-0">
                                        {event.title}
                                    </h3>
                                    <span
                                        className={cn(
                                            'shrink-0 text-[10px] px-2 py-0.5 rounded-full font-semibold',
                                            typeBadgeClass[event.type],
                                        )}
                                    >
                                        {typeLabelMap[event.type]}
                                    </span>
                                </div>
                                <p className="text-xs text-muted-foreground line-clamp-2 leading-relaxed">
                                    {event.description}
                                </p>
                                <div className="flex items-center gap-1 mt-1">
                                    <MapPin size={11} className="text-muted-foreground shrink-0" />
                                    <span className="text-xs text-muted-foreground truncate">{event.location}</span>
                                    {event.startTime && (
                                        <>
                                            <span className="text-muted-foreground/40 text-xs">·</span>
                                            <span className="text-xs text-muted-foreground shrink-0">{event.startTime} hs</span>
                                        </>
                                    )}
                                </div>
                            </div>
                        </div>
                    )
                })}
            </div>
        </section>
    )
}
