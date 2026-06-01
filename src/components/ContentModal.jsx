const ContentModal = ({ term, onClose }) => {
  const parseContent = (text) => {
    return text.split(/(\*\*.*?\*\*)/).map((part, i) =>
      part.startsWith('**') && part.endsWith('**')
        ? <strong key={i}>{part.slice(2, -2)}</strong>
        : part
    );
  };

  return (
    <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-[70]" onClick={onClose}>
      <div className="bg-white font-[Inter] text-[#515151] w-full mt-5 max-w-2xl p-9 rounded-2xl shadow-2xl relative" onClick={e => e.stopPropagation()}>
        <button onClick={onClose} className="absolute top-6 right-6 text-gray-400 hover:text-gray-600 text-lg font-bold">✕</button>
        <h3 className="text-lg font-bold mb-4">{term.title}</h3>
        <p className="text-sm whitespace-pre-wrap leading-relaxed overflow-y-auto max-h-[60vh]">{parseContent(term.content)}</p>
      </div>
    </div>
  );
};

export default ContentModal;