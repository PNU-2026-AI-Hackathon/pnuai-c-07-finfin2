export default function Tag({ label, selected, onClick }) {
  return (
    <button
      type="button"
      onClick={onClick}
      className={`px-3 py-1.5 rounded-full border text-sm transition-all cursor-pointer ${
        selected
          ? "bg-teal-500 text-white border-teal-500"
          : "bg-white text-gray-600 border-gray-300 hover:border-teal-400"
      }`}
    >
      #{label}
    </button>
  );
}