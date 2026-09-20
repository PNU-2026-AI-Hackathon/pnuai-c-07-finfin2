const AgreementItem = ({ id, label, checked, onChange, onView }) => (
  <div className="flex items-center justify-between p-4 bg-[#EFEFEF] rounded-lg mb-2">
    <div className="flex items-center gap-3">
      <div
        onClick={() => onChange(id)}
        className={`w-5 h-5 rounded-sm border-2 flex items-center justify-center cursor-pointer transition-colors ${
          checked ? 'bg-[#03BFA5] border-[#03BFA5]' : 'bg-white border-white'
        }`}
      >
        {checked && (
          <svg className="w-3 h-3 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={3}>
            <path strokeLinecap="round" strokeLinejoin="round" d="M5 13l4 4L19 7" />
          </svg>
        )}
      </div>
      <span className="text-sm">{label}</span>
    </div>
    {onView && (
      <button onClick={onView} className="text-xs text-[#515151] underline hover:text-gray-400 transition-colors">
        전문보기
      </button>
    )}
  </div>
);

export default AgreementItem;