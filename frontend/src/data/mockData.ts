import type { CareerInfo, ForumNotice, RecentItem, UpcomingEvent } from '@/types/content.types'

export const recentItems: RecentItem[] = [
    {
        id: '1',
        type: 'course',
        title: 'Análisis Matemático I',
        subtitle: 'Ingeniería en Sistemas de Información · 1er año',
        accessedAt: new Date(Date.now() - 1000 * 60 * 35).toISOString(), // hace 35 min
        href: '/cursos/1',
    },
    {
        id: '2',
        type: 'forum',
        title: '¿Conviene ir a la EXPO UTN este año?',
        subtitle: 'Foro General · Comunidad',
        accessedAt: new Date(Date.now() - 1000 * 60 * 60 * 3).toISOString(), // hace 3 hs
        href: '/comunidad/foros/2',
    },
    {
        id: '3',
        type: 'apunte',
        title: 'Resumen Sistemas de Representación',
        subtitle: 'Compartido por M. González · Biblioteca',
        accessedAt: new Date(Date.now() - 1000 * 60 * 60 * 24).toISOString(), // hace 1 día
        href: '/biblioteca/3',
    },
    {
        id: '4',
        type: 'course',
        title: 'Algoritmos y Estructuras de Datos',
        subtitle: 'Ingeniería en Sistemas de Información · 2do año',
        accessedAt: new Date(Date.now() - 1000 * 60 * 60 * 48).toISOString(), // hace 2 días
        href: '/cursos/4',
    },
    {
        id: '5',
        type: 'forum',
        title: 'Reclamo: fechas del parcial de Física II',
        subtitle: 'Foro Reclamos · Comunidad',
        accessedAt: new Date(Date.now() - 1000 * 60 * 60 * 72).toISOString(), // hace 3 días
        href: '/comunidad/foros/5',
    },
]

export const upcomingEvents: UpcomingEvent[] = [
    {
        id: '1',
        title: 'Parcial: Análisis Matemático I',
        description: 'Primer parcial de Análisis Matemático I. Temario: límites, continuidad, derivadas e integrales de funciones de una variable.',
        date: '2026-04-10',
        startTime: '08:00',
        location: 'Aula 201 — FRBA',
        type: 'presencial',
    },
    {
        id: '2',
        title: 'Entrega TP: Programación I',
        description: 'Fecha límite para la entrega del Trabajo Práctico N°2 de Programación I. Subir al aula virtual antes de las 23:59.',
        date: '2026-04-08',
        startTime: '23:59',
        location: 'Aula Virtual — Moodle',
        type: 'virtual',
    },
    {
        id: '3',
        title: 'Charla: IA en la industria argentina',
        description: 'Charla abierta organizada por el Centro de Estudiantes sobre las aplicaciones de la Inteligencia Artificial en el mercado laboral argentino.',
        date: '2026-04-15',
        startTime: '18:00',
        location: 'Zoom — Acceso por Moodle',
        type: 'virtual',
    },
    {
        id: '4',
        title: 'EXPO UTN 2026',
        description: 'Exposición anual de proyectos finales y trabajos de investigación de estudiantes y docentes de todas las facultades regionales.',
        date: '2026-05-03',
        startTime: '10:00',
        location: 'Campus Central — FRBA',
        type: 'presencial',
    },
    {
        id: '5',
        title: 'Jornada de Ingeniería Civil',
        description: 'Jornada interdisciplinaria sobre construcción sustentable y gestión de obras. Con expositores de la industria y el ámbito académico.',
        date: '2026-04-22',
        startTime: '09:00',
        location: 'Auditorio FRBA / Transmisión en vivo',
        type: 'hibrido',
    },
]

export const latestNotice: ForumNotice = {
    id: '1',
    title: 'Modificación en el cronograma de exámenes finales — 1° cuatrimestre 2026',
    content:
        'Por disposición de la Dirección de Asuntos Estudiantiles, se informa a todos los alumnos y alumnas que el cronograma de exámenes finales del primer cuatrimestre de 2026 ha sido actualizado. Las fechas publicadas en el sistema de autogestión reflejan los cambios aprobados en la última reunión de Consejo Departamental del 18 de marzo de 2026.\n\nLos exámenes que contaban con fechas entre el 28 de abril y el 3 de mayo fueron reprogramados para la semana del 6 al 10 de mayo, a fin de evitar superposiciones con la EXPO UTN 2026. Se solicita a los inscriptos verificar sus horarios y salones asignados en la plataforma de autogestión antes del 1° de abril.\n\nCualquier inconsistencia o reclamo deberá ser presentado formalmente a través del sistema de gestión estudiantil, en el período habilitado del 24 al 31 de marzo. No se tomarán reclamos fuera de ese plazo. Para consultas generales, comunicarse con la Secretaría de Alumnos en el horario de atención de 9 a 13 hs.\n\nSe recuerda que la asistencia al examen implica tener el DNI vigente y la libreta universitaria al día. Cualquier alumno que no cumpla con los requisitos de regularidad no podrá presentarse, independientemente de la inscripción realizada. Agradecemos la comprensión y colaboración de toda la comunidad estudiantil.',
    author: 'Dirección de Asuntos Estudiantiles — UTN FRBA',
    postedAt: new Date(Date.now() - 1000 * 60 * 60 * 6).toISOString(), // hace 6 hs
}

export const careers: CareerInfo[] = [
    {
        id: 1,
        name: 'Ingeniería en Sistemas de Información',
        faculty: 'FRBA — Buenos Aires',
        durationYears: 5,
        type: 'presencial',
        description: 'Formación en desarrollo de software, bases de datos, redes y gestión de proyectos tecnológicos. Una de las ingenierías más demandadas del país.',
    },
    {
        id: 2,
        name: 'Ingeniería Industrial',
        faculty: 'FRA — Avellaneda',
        durationYears: 5,
        type: 'presencial',
        description: 'Optimización de procesos productivos, gestión de operaciones y mejora continua. Amplia inserción en la industria manufacturera y de servicios.',
    },
    {
        id: 3,
        name: 'Ingeniería Electrónica',
        faculty: 'FRD — Delta',
        durationYears: 5,
        type: 'presencial',
        description: 'Diseño de circuitos, sistemas embebidos, telecomunicaciones y automatización. Fuerte base en electrónica analógica y digital.',
    },
    {
        id: 4,
        name: 'Ingeniería Mecánica',
        faculty: 'FRC — Córdoba',
        durationYears: 5,
        type: 'presencial',
        description: 'Diseño y análisis de máquinas, termodinámica, fluidos y manufactura. Base sólida en ciencias exactas y aplicadas al sector industrial.',
    },
    {
        id: 5,
        name: 'Ingeniería Civil',
        faculty: 'FRBA — Buenos Aires',
        durationYears: 5,
        type: 'presencial',
        description: 'Planificación y construcción de obras civiles, estructuras, hidráulica y gestión ambiental. Alta demanda en el sector de la construcción.',
    },
    {
        id: 6,
        name: 'Ingeniería Química',
        faculty: 'FRRo — Rosario',
        durationYears: 5,
        type: 'presencial',
        description: 'Procesos industriales de transformación química, control de calidad, seguridad industrial y gestión ambiental en plantas de producción.',
    },
    {
        id: 7,
        name: 'Ingeniería Textil',
        faculty: 'FRGP — General Pacheco',
        durationYears: 5,
        type: 'presencial',
        description: 'Tecnología aplicada a fibras, tejidos y procesos textiles. Formación en diseño de productos, control de calidad y gestión industrial del sector.',
    },
    {
        id: 8,
        name: 'Licenciatura en Organización Industrial',
        faculty: 'FRA — Avellaneda',
        durationYears: 4,
        type: 'virtual',
        description: 'Gestión empresarial, logística, administración de operaciones y análisis estratégico. Modalidad virtual orientada a profesionales en actividad.',
    },
]
