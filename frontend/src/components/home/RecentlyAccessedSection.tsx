import { BookOpen, GraduationCap, MessageSquare } from 'lucide-react'
import type { LucideIcon } from 'lucide-react'
import { Link } from 'react-router'
import { cn } from '@/lib/utils'
import { SectionHeader } from '@/components/ui/SectionHeader'
import type { ContentItemType, RecentItem } from '@/types/content.types'

interface RecentlyAccessedSectionProps {
    items: RecentItem[]
}

const iconByType: Record<ContentItemType, LucideIcon> = {
    apunte: BookOpen,
    course: GraduationCap,
    forum: MessageSquare,
}

const labelByType: Record<ContentItemType, string> = {
    apunte: 'Apunte',
    course: 'Curso',
    forum: 'Foro',
}

function formatRelativeTime(isoString: string): string {
    const diffMs = Date.now() - new Date(isoString).getTime()
    const diffMins = Math.floor(diffMs / 60_000)
    if (diffMins < 1) return 'Hace un momento'
    if (diffMins < 60) return `Hace ${diffMins} min`
    const diffHours = Math.floor(diffMins / 60)
    if (diffHours < 24) return `Hace ${diffHours} h`
    const diffDays = Math.floor(diffHours / 24)
    return `Hace ${diffDays} día${diffDays > 1 ? 's' : ''}`
}

export function RecentlyAccessedSection({ items }: RecentlyAccessedSectionProps) {
    return (
        <section className="animate-in fade-in slide-in-from-bottom-2 duration-300">
            <SectionHeader title="Accedido recientemente" />
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-5 gap-3">
                {items.map((item) => {
                    const Icon = iconByType[item.type]
                    return (
                        <Link
                            key={item.id}
                            to={item.href}
                            className={cn(
                                'group flex flex-col gap-3 rounded-xl border border-border bg-card p-4',
                                'hover:border-primary/30 hover:shadow-md transition-all duration-200 cursor-pointer',
                            )}
                        >
                            <div className="flex items-start justify-between gap-2">
                                <div className="w-9 h-9 rounded-lg bg-primary/10 flex items-center justify-center shrink-0">
                                    <Icon size={16} className="text-primary" />
                                </div>
                                <span className="text-[10px] font-medium text-muted-foreground bg-secondary px-1.5 py-0.5 rounded-full shrink-0 mt-0.5">
                                    {labelByType[item.type]}
                                </span>
                            </div>
                            <div className="flex flex-col gap-1 min-w-0">
                                <p className="text-sm font-semibold text-foreground line-clamp-2 leading-snug group-hover:text-primary transition-colors duration-150">
                                    {item.title}
                                </p>
                                <p className="text-xs text-muted-foreground line-clamp-2">{item.subtitle}</p>
                            </div>
                            <p className="text-[10px] text-muted-foreground mt-auto">{formatRelativeTime(item.accessedAt)}</p>
                        </Link>
                    )
                })}
            </div>
        </section>
    )
}
