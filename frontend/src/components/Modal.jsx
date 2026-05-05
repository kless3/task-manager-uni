function Modal({ title, isOpen, onClose, children }) {
  if (!isOpen) return null;

  return (
    <div className="modal-backdrop" role="dialog" aria-modal="true" onClick={onClose}>
      <div className="modal-card" onClick={(event) => event.stopPropagation()}>
        <div className="modal-head">
          <h3>{title}</h3>
          <button type="button" className="secondary" onClick={onClose}>
            Close
          </button>
        </div>
        <div className="modal-body">{children}</div>
      </div>
    </div>
  );
}

export default Modal;
