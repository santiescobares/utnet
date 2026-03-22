import { useState } from 'react'
import { cn } from '@/lib/utils'
import { SectionHeader } from '@/components/ui/SectionHeader'
import type { ForumNotice } from '@/types/content.types'

interface LastNoticeSectionProps {
    notice: ForumNotice
}

function formatDate(isoString: string): string {
    return new Date(isoString).toLocaleDateString('es-AR', {
        day: 'numeric',
        month: 'long',
        year: 'numeric',
    })
}

export function LastNoticeSection({ notice }: LastNoticeSectionProps) {
    const [isExpanded, setIsExpanded] = useState(false)

    return (
        <section className="animate-in fade-in slide-in-from-bottom-2 duration-300">
            <SectionHeader title="Último aviso" />
            <div className="rounded-xl border border-border/80 border-l-4 border-l-primary bg-card p-5 sm:p-6">
                {/* Author + date */}
                <div className="flex items-center gap-2 mb-3 flex-wrap">
                    <span className="text-xs font-semibold text-foreground">{notice.author}</span>
                    <span className="text-muted-foreground/40 text-xs">·</span>
                    <time className="text-xs text-muted-foreground">{formatDate(notice.postedAt)}</time>
                </div>

                {/* Title */}
                <h3 className="text-base font-bold text-foreground mb-3 leading-snug">{notice.title}</h3>

                {/* Content with fade */}
                <div className="relative">
                    <p
                        className={cn(
                            'text-sm text-muted-foreground leading-relaxed whitespace-pre-line transition-all duration-300',
                            !isExpanded && 'line-clamp-3',
                        )}
                    >
                        {notice.content}
                    </p>

                    {/* Fade overlay when collapsed */}
                    {!isExpanded && (
                        <div className="absolute bottom-0 left-0 right-0 h-8 bg-gradient-to-t from-card to-transparent pointer-events-none" />
                    )}
                </div>

                <button
                    onClick={() => setIsExpanded((p) => !p)}
                    className="mt-4 text-xs font-semibold text-primary hover:text-primary/80 transition-colors duration-150 inline-flex items-center gap-1"
                >
                    {isExpanded ? 'Ver menos ↑' : 'Ver más ↓'}
                </button>
            </div>
        </section>
    )
}
