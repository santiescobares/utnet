interface SectionHeaderProps {
    title: string
    action?: React.ReactNode
}

export function SectionHeader({ title, action }: SectionHeaderProps) {
    return (
        <div className="flex items-center justify-between mb-5">
            <h2 className="text-xl font-bold tracking-tight text-foreground">{title}</h2>
            {action && <div className="shrink-0">{action}</div>}
        </div>
    )
}
