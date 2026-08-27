const baseInput = "border border-[#D9D9D9] rounded-[4px] px-3 text-[18px] text-[#454545] focus:outline-none focus:border-[#03BFA5]";
const baseSelect = "border border-[#D9D9D9] rounded-[4px] px-3 text-[18px] text-[#454545] focus:outline-none focus:border-[#03BFA5]";

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
