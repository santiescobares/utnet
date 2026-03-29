interface StudyRecordDescriptionBlockProps {
    description: string
}

export function StudyRecordDescriptionBlock({ description }: StudyRecordDescriptionBlockProps) {
    return (
        <p className="text-sm text-muted-foreground leading-relaxed whitespace-pre-wrap">
            {description}
        </p>
    )
}
