export function formatDate(value) {
  if (!value) return "-";
  return value.replace("T", " ").slice(0, 16);
}

export function toArray(value) {
  return Array.isArray(value) ? value : [];
}

export function sortMeetingsByDate(meetings) {
  return [...toArray(meetings)].sort((a, b) => (a.meetingDate || "").localeCompare(b.meetingDate || ""));
}
