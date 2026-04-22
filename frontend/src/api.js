const API_PREFIX = "/api/v1";

function toQuery(params = {}) {
  const query = new URLSearchParams();
  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== "") {
      query.append(key, value);
    }
  });
  const str = query.toString();
  return str ? `?${str}` : "";
}

async function request(path, options = {}) {
  const response = await fetch(`${API_PREFIX}${path}`, {
    headers: {
      "Content-Type": "application/json",
      ...(options.headers ?? {}),
    },
    ...options,
  });

  const text = await response.text();
  let payload = null;

  if (text) {
    try {
      payload = JSON.parse(text);
    } catch {
      payload = text;
    }
  }

  if (!response.ok) {
    const message =
      payload?.message ||
      (payload?.errors ? Object.values(payload.errors).join("; ") : null) ||
      response.statusText ||
      "Request failed";
    throw new Error(message);
  }

  return payload;
}

export const api = {
  getProjects: () => request("/projects"),
  getProjectsByStatus: (status) => request(`/projects/find${toQuery({ status })}`),
  createProject: (body) => request("/projects", { method: "POST", body: JSON.stringify(body) }),
  updateProject: (id, body) => request(`/projects/${id}`, { method: "PUT", body: JSON.stringify(body) }),
  deleteProject: (id) => request(`/projects/${id}`, { method: "DELETE" }),

  getTasks: () => request("/tasks"),
  createTask: (projectId, body) =>
    request(`/tasks/project/${projectId}`, { method: "POST", body: JSON.stringify(body) }),
  updateTask: (id, body) => request(`/tasks/${id}`, { method: "PUT", body: JSON.stringify(body) }),
  deleteTask: (id) => request(`/tasks/${id}`, { method: "DELETE" }),

  getMeetings: (params) => request(`/meetings${toQuery(params)}`),
  getMeetingsByProject: (projectId) => request(`/meetings/byProject/${projectId}`),
  createMeeting: (projectId, body) =>
    request(`/meetings/project/${projectId}`, { method: "POST", body: JSON.stringify(body) }),
  updateMeeting: (id, body) => request(`/meetings/${id}`, { method: "PUT", body: JSON.stringify(body) }),
  deleteMeeting: (id) => request(`/meetings/${id}`, { method: "DELETE" }),

  getLabels: () => request("/labels"),
  createLabel: (taskId, body) =>
    request(`/labels/task/${taskId}`, { method: "POST", body: JSON.stringify(body) }),
  attachLabelToTask: (labelId, taskId) =>
    request(`/labels/${labelId}/attach/task/${taskId}`, { method: "POST" }),
  updateLabel: (id, body) => request(`/labels/${id}`, { method: "PUT", body: JSON.stringify(body) }),
  deleteLabel: (id) => request(`/labels/${id}`, { method: "DELETE" }),

  getCommentsByTask: (taskId) => request(`/comments/byTask/${taskId}`),
  createComment: (taskId, body) =>
    request(`/comments/task/${taskId}`, { method: "POST", body: JSON.stringify(body) }),
  updateComment: (id, body) => request(`/comments/${id}`, { method: "PUT", body: JSON.stringify(body) }),
  deleteComment: (id) => request(`/comments/${id}`, { method: "DELETE" }),
};
