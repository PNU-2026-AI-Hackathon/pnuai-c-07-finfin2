const baseInput = "border border-gray-300 rounded-lg px-3 py-1.5 text-sm focus:outline-none focus:border-teal-400";
const baseSelect = "border border-gray-300 rounded-lg px-3 py-2 text-sm text-gray-700 focus:outline-none focus:border-teal-400";

export function FormInput({ className = "", ...props }) {
  return <input className={`${baseInput} ${className}`} {...props} />;
}

export function FormSelect({ children, className = "", ...props }) {
  return (
    <select className={`${baseSelect} ${className}`} {...props}>
      {children}
    </select>
  );
}