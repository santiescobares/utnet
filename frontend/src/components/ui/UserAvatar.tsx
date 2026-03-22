import { cn } from '@/lib/utils'

interface UserAvatarProps {
    firstName: string
    lastName: string
    pictureURL?: string | null
    size?: 'sm' | 'md' | 'lg'
    className?: string
}

const sizeMap: Record<NonNullable<UserAvatarProps['size']>, string> = {
    sm: 'w-8 h-8 text-xs',
    md: 'w-10 h-10 text-sm',
    lg: 'w-14 h-14 text-base',
}

export function UserAvatar({ firstName, lastName, pictureURL, size = 'sm', className }: UserAvatarProps) {
    const sizeClass = sizeMap[size]
    const initials = `${firstName[0] ?? '?'}${lastName[0] ?? '?'}`.toUpperCase()

    const base = cn('rounded-full shrink-0 overflow-hidden flex items-center justify-center font-semibold', sizeClass, className)

    if (pictureURL) {
        return (
            <div className={base}>
                <img
                    src={pictureURL}
                    alt={`${firstName} ${lastName}`}
                    className="w-full h-full object-cover"
                />
            </div>
        )
    }

    return (
        <div className={cn(base, 'bg-primary text-primary-foreground select-none')}>
            {initials}
        </div>
    )
}
