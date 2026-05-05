function ProjectListView({
  filteredProjects,
  projectSearch,
  onProjectSearchChange,
  projectStatusFilter,
  onProjectStatusFilterChange,
  projectStatuses,
  onRefresh,
  onOpenProject,
  onOpenCreateProjectModal,
  onStartEditProject,
  onRemoveProject,
  formatDate,
  toArray,
}) {
  return (
    <section className="panel list-shell">
      <div className="list-toolbar">
        <input
          placeholder="Search projects"
          value={projectSearch}
          onChange={(event) => onProjectSearchChange(event.target.value)}
        />
        <select value={projectStatusFilter} onChange={(event) => onProjectStatusFilterChange(event.target.value)}>
          <option value="">All statuses</option>
          {projectStatuses.map((status) => (
            <option key={status} value={status}>
              {status}
            </option>
          ))}
        </select>
        <div className="btn-group">
          <button className="secondary" onClick={onRefresh}>
            Refresh
          </button>
          <button onClick={onOpenCreateProjectModal}>Create project</button>
        </div>
      </div>

      <div className="project-grid">
        {filteredProjects.map((project) => (
          <article key={project.id} className="project-tile">
            <div className="tile-top">
              <h3>{project.title}</h3>
              <span className="status-pill">{project.status}</span>
            </div>
            <p className="meta">{project.description}</p>
            <p className="meta">
              {toArray(project.tasks).length} tasks - {toArray(project.meetings).length} meetings
            </p>
            <p className="meta">
              {formatDate(project.startDate)} -&gt; {formatDate(project.deadline)}
            </p>
            <div className="btn-group">
              <button onClick={() => onOpenProject(project)}>Open project</button>
              <button className="secondary" onClick={() => onStartEditProject(project)}>
                Edit
              </button>
              <button className="danger" onClick={() => onRemoveProject(project.id)}>
                Delete
              </button>
            </div>
          </article>
        ))}
      </div>

      {!filteredProjects.length && <p className="small-note">No projects found.</p>}
    </section>
  );
}

export default ProjectListView;
