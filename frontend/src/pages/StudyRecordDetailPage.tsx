import { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router'
import {
    ArrowLeft,
    Download,
    Flag,
    Loader2,
    Pencil,
    UserCircle,
    BookOpen,
    AlertCircle,
} from 'lucide-react'
import { toast } from 'sonner'

import { studyRecordService } from '@/services/studyRecord.service'
import { reportService } from '@/services/report.service'
import { userService } from '@/services/user.service'
import { useAuthStore } from '@/store/authStore'
import { useActivityStore } from '@/store/activityStore'
import type { StudyRecordDTO, SubjectSoftDTO } from '@/types/studyrecord.types'
import { UserAvatar } from '@/components/ui/UserAvatar'
import { ConfirmActionModal } from '@/components/ui/ConfirmActionModal'
import { StudyRecordBadges } from '@/components/library/StudyRecordBadges'
import { StudyRecordDescriptionBlock } from '@/components/library/StudyRecordDescriptionBlock'
import { StudyRecordPreviewPanel } from '@/components/library/StudyRecordPreviewPanel'
import { StudyRecordEditModal } from '@/components/library/StudyRecordEditModal'

function formatFileSize(bytes: number): string {
    if (bytes < 1024) return `${bytes} B`
    if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`
    return `${(bytes / (1024 * 1024)).toFixed(2)} MB`
}

function getFileTypeLabel(previewUrl: string): string {
    const ext = previewUrl.split('?')[0].split('.').pop()?.toLowerCase()
    const labels: Record<string, string> = {
        pdf: 'PDF',
        doc: 'Word',
        docx: 'Word',
        png: 'Imagen PNG',
        jpg: 'Imagen JPG',
        jpeg: 'Imagen JPG',
    }
    return ext ? (labels[ext] ?? ext.toUpperCase()) : 'Archivo'
}

export function StudyRecordDetailPage() {
    const { slug } = useParams<{ slug: string }>()
    const navigate = useNavigate()
    const { user } = useAuthStore()
    const { recentItems, addItem } = useActivityStore()

    const [record, setRecord] = useState<StudyRecordDTO | null>(null)
    const [subject, setSubject] = useState<SubjectSoftDTO | null>(null)
    const [previewUrl, setPreviewUrl] = useState<string | null>(null)
    const [loading, setLoading] = useState(true)
    const [notFound, setNotFound] = useState(false)
    const [loadError, setLoadError] = useState<string | null>(null)
    const [isDownloading, setIsDownloading] = useState(false)
    const [showEditModal, setShowEditModal] = useState(false)
    const [showReportModal, setShowReportModal] = useState(false)
    const [reportReason, setReportReason] = useState('')
    const [showReportConfirm, setShowReportConfirm] = useState(false)
    const [isReporting, setIsReporting] = useState(false)

    // Load record + subject in parallel
    useEffect(() => {
        if (!slug) return
        setLoading(true)
        setNotFound(false)
        setLoadError(null)

        studyRecordService.getBySlug(slug)
            .then((rec) => {
                setRecord(rec)
                const found = rec.subjects[0] ?? null
                setSubject(found)
                const last = recentItems[0]
                const isSameAsLast = last?.type === 'apunte' && last.id === rec.slug
                if (!isSameAsLast) {
                    const recordAccessedAt = new Date().toISOString()
                    userService.addRecentActivity({ resourceType: 'STUDY_RECORD', resourceId: rec.slug, timestamp: recordAccessedAt })
                        .catch(() => { /* silencioso */ })
                    const career = found?.careers[0]
                    addItem({
                        id: rec.slug,
                        type: 'apunte',
                        title: rec.title,
                        subtitle: career && found ? `${career.name} · ${found.name}` : (found?.name ?? ''),
                        href: `/library/${rec.slug}`,
                        accessedAt: recordAccessedAt,
                    })
                }
            })
            .catch((err) => {
                const status = err?.response?.status
                if (status === 404) {
                    setNotFound(true)
                } else {
                    setLoadError('Ocurrió un error al cargar el recurso.')
                }
            })
            .finally(() => setLoading(false))
    }, [slug, addItem])

    // Non-blocking preview URL fetch
    useEffect(() => {
        if (!record) return
        studyRecordService.getPreviewUrl(record.id).then(setPreviewUrl).catch(() => {
            // Preview failure is non-critical; the panel handles the null case
        })
    }, [record?.id])

    const isOwner = !!(user && record && user.id === record.createdBy.id)
    const isAdmin = user?.role === 'ADMINISTRATOR'
    const canEdit = isOwner || isAdmin

    const handleReport = async () => {
        if (!record) return
        setIsReporting(true)
        try {
            await reportService.create({
                resourceType: 'STUDY_RECORD',
                resourceId: String(record.id),
                reason: reportReason.trim(),
            })
            setShowReportModal(false)
            setShowReportConfirm(false)
            setReportReason('')
            toast.success('Reporte enviado.')
        } catch {
            toast.error('No se pudo enviar el reporte.')
        } finally {
            setIsReporting(false)
        }
    }

    const handleDownload = async () => {
        if (!record) return
        setIsDownloading(true)
        try {
            const url = await studyRecordService.getDownloadUrl(record.id)
            const response = await fetch(url)
            const blob = await response.blob()
            const objectUrl = URL.createObjectURL(blob)
            const a = document.createElement('a')
            a.href = objectUrl
            a.download = record.title
            a.style.display = 'none'
            document.body.appendChild(a)
            a.click()
            document.body.removeChild(a)
            URL.revokeObjectURL(objectUrl)
        } catch {
            toast.error('No se pudo descargar el archivo. Intentá de nuevo.')
        } finally {
            setIsDownloading(false)
        }
    }

    // ── Loading ──────────────────────────────────────────────────────────────
    if (loading) {
        return (
            <div className="flex items-center justify-center min-h-[calc(100vh-4rem)]">
                <Loader2 className="animate-spin text-muted-foreground" size={32} />
            </div>
        )
    }

    // ── Not found ────────────────────────────────────────────────────────────
    if (notFound) {
        return (
            <div className="flex flex-col items-center justify-center gap-4 min-h-[calc(100vh-4rem)] px-6 text-center">
                <BookOpen className="text-muted-foreground" size={40} />
                <p className="text-base font-semibold text-foreground">Este recurso no existe o fue eliminado.</p>
                <button
                    onClick={() => navigate('/library')}
                    className="text-sm text-primary hover:underline"
                >
                    Volver a Biblioteca
                </button>
            </div>
        )
    }

    // ── Error ────────────────────────────────────────────────────────────────
    if (loadError || !record) {
        return (
            <div className="flex flex-col items-center justify-center gap-4 min-h-[calc(100vh-4rem)] px-6 text-center">
                <AlertCircle className="text-destructive" size={40} />
                <p className="text-base font-semibold text-foreground">{loadError ?? 'Error desconocido'}</p>
                <button
                    onClick={() => navigate('/library')}
                    className="text-sm text-primary hover:underline"
                >
                    Volver a Biblioteca
                </button>
            </div>
        )
    }

    // ── Page ─────────────────────────────────────────────────────────────────
    const authorName = isOwner
        ? `${user!.firstName} ${user!.lastName}`
        : 'un colaborador'

    return (
        <div className="flex flex-col lg:flex-row min-h-[calc(100vh-4rem)]">
            {/* ── Left panel ─────────────────────────────────────────── */}
            <div className="lg:w-1/2 overflow-y-auto px-4 sm:px-6 py-6 flex flex-col gap-5 lg:border-r border-border">
                {/* Top action bar */}
                <div className="flex items-center justify-between">
                    <button
                        onClick={() => navigate('/library')}
                        className="flex items-center gap-1.5 text-sm text-muted-foreground hover:text-foreground transition-colors"
                    >
                        <ArrowLeft size={16} />
                        Volver a Biblioteca
                    </button>

                    <div className="flex items-center gap-2">
                        {canEdit && (
                            <button
                                onClick={() => setShowEditModal(true)}
                                className="p-1.5 rounded-lg text-muted-foreground hover:text-foreground hover:bg-secondary transition-colors"
                                aria-label="Editar publicación"
                            >
                                <Pencil size={16} />
                            </button>
                        )}
                        <button
                            onClick={() => setShowReportModal(true)}
                            className="p-1.5 rounded-lg text-muted-foreground hover:text-foreground hover:bg-secondary transition-colors"
                            aria-label="Reportar publicación"
                        >
                            <Flag size={16} />
                        </button>
                    </div>
                </div>

                {/* Title */}
                <div className="flex items-start gap-3 flex-wrap">
                    <h1 className="text-2xl sm:text-3xl font-bold text-foreground leading-tight">
                        {record.title}
                    </h1>
                    {record.hidden && (
                        <span className="self-center inline-flex items-center px-2 py-0.5 rounded-full text-[11px] font-semibold border border-muted-foreground/30 bg-muted-foreground/10 text-muted-foreground shrink-0">
                            Oculto
                        </span>
                    )}
                </div>

                {/* Badges */}
                <StudyRecordBadges record={record} subject={subject} />

                {/* Author row */}
                <button
                    onClick={() => navigate(`/users/${record.createdBy.id}`)}
                    className="flex items-center gap-2 hover:opacity-80 transition-opacity cursor-pointer"
                >
                    {isOwner ? (
                        <UserAvatar
                            firstName={user!.firstName}
                            lastName={user!.lastName}
                            pictureURL={user!.profile.pictureURL}
                            size="sm"
                            className="w-6 h-6 text-[10px]"
                        />
                    ) : (
                        <UserCircle size={22} className="text-muted-foreground shrink-0" />
                    )}
                    <span className="text-xs text-muted-foreground">
                        Publicado por <span className="font-medium text-foreground">{authorName}</span>
                        {' · '}
                        <span>{record.downloads} descargas</span>
                    </span>
                </button>

                {/* Description */}
                <StudyRecordDescriptionBlock description={record.description} />

                {/* File info + Download button */}
                <div className="mt-auto flex flex-col gap-2">
                    <p className="text-xs text-muted-foreground">
                        {previewUrl
                            ? `Tipo: ${getFileTypeLabel(previewUrl)} · Tamaño: ${formatFileSize(record.resourceSize)}`
                            : `Tamaño: ${formatFileSize(record.resourceSize)}`}
                    </p>
                    <button
                        onClick={handleDownload}
                        disabled={isDownloading}
                        className="w-full flex items-center justify-center gap-2 py-3 rounded-xl bg-primary text-primary-foreground font-semibold text-sm transition-opacity hover:opacity-90 disabled:opacity-60"
                    >
                        {isDownloading ? (
                            <Loader2 size={16} className="animate-spin" />
                        ) : (
                            <Download size={16} />
                        )}
                        Descargar
                    </button>
                </div>
            </div>

            {/* ── Right panel ────────────────────────────────────────── */}
            <div className="w-full lg:w-1/2 lg:sticky lg:top-16 lg:h-[calc(100vh-4rem)] flex flex-col border-t lg:border-t-0 border-border">
                <StudyRecordPreviewPanel previewUrl={previewUrl} />
            </div>

            {/* ── Edit modal ─────────────────────────────────────────── */}
            {showEditModal && (
                <StudyRecordEditModal
                    record={record}
                    currentSubject={subject}
                    onClose={() => setShowEditModal(false)}
                    onSaved={(updated) => {
                        setRecord(updated)
                        setShowEditModal(false)
                    }}
                    onDeleted={() => navigate('/library')}
                />
            )}

            {/* ── Report modal ───────────────────────────────────────── */}
            {showReportModal && (
                <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
                    <div
                        className="absolute inset-0 bg-black/50 backdrop-blur-sm animate-in fade-in duration-200"
                        onClick={() => { setShowReportModal(false); setReportReason('') }}
                    />
                    <div className="relative z-10 w-full max-w-md bg-card border border-border rounded-2xl shadow-2xl p-6 flex flex-col gap-4 animate-in fade-in zoom-in-95 duration-200">
                        <div>
                            <h2 className="text-base font-bold text-foreground">Reportar recurso</h2>
                            <p className="text-xs text-muted-foreground mt-1">
                                Describí por qué este recurso viola las normas de la comunidad.
                            </p>
                        </div>
                        <div className="flex flex-col gap-1.5">
                            <textarea
                                value={reportReason}
                                onChange={(e) => setReportReason(e.target.value)}
                                maxLength={500}
                                rows={4}
                                placeholder="Ej: El contenido no corresponde a la materia, contiene información incorrecta…"
                                className="w-full px-3 py-2.5 text-sm rounded-xl border bg-secondary border-border text-foreground resize-none focus:outline-none focus:ring-2 focus:ring-primary/30 focus:border-primary/50 transition-all duration-150 placeholder:text-muted-foreground"
                            />
                            <span className="text-[11px] text-muted-foreground text-right">{reportReason.length}/500</span>
                        </div>
                        <div className="flex items-center justify-end gap-2">
                            <button
                                onClick={() => { setShowReportModal(false); setReportReason('') }}
                                className="px-4 py-2.5 text-sm font-medium rounded-xl border border-border bg-card text-foreground hover:bg-secondary transition-colors duration-150"
                            >
                                Cancelar
                            </button>
                            <button
                                onClick={() => setShowReportConfirm(true)}
                                disabled={reportReason.trim().length < 10}
                                className="px-4 py-2.5 text-sm font-medium rounded-xl bg-destructive text-destructive-foreground hover:bg-destructive/90 transition-colors duration-150 disabled:opacity-40 disabled:cursor-not-allowed"
                            >
                                Reportar
                            </button>
                        </div>
                    </div>
                </div>
            )}

            <ConfirmActionModal
                open={showReportConfirm}
                onClose={() => setShowReportConfirm(false)}
                onConfirm={handleReport}
                title="¿Confirmar reporte?"
                description="Tu reporte será revisado por el equipo de moderación."
                confirmLabel="Confirmar"
                confirmVariant="destructive"
                isLoading={isReporting}
            />
        </div>
    )
}
