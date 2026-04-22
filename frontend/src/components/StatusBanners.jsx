function StatusBanners({ success, error }) {
  return (
    <>
      {success && <div className="status-bar status-success">{success}</div>}
      {error && <div className="status-bar status-error">{error}</div>}
    </>
  );
}

export default StatusBanners;
