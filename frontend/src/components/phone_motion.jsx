import { motion } from "framer-motion";
import styled from "styled-components";
import leftphone from '../assets/phone_left.png'
import rightphone from '../assets/phone_right.png'
import centerphone from '../assets/phone_center.png'

const AnimationContainer = styled.div`
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  height: 500px;
  overflow: hidden;
`;

const PhoneWrapper = styled(motion.div)`
  position: absolute;
  width: 300px; 
  z-index: ${(props) => props.$zIndex};
  img {
    width: 100%;
    height: auto; 
    display: block; 
  }
`;

const PhoneAnimation = () => {
  return (
    <AnimationContainer>
      {/* 왼쪽 휴대폰 */}
      <PhoneWrapper
        $zIndex={1}
        initial={{ x: 0, opacity: 0, rotate: 0 }}
        whileInView={{ x: -180, opacity: 1, rotate: -10 }} // 화면에 들어오면 실행
        viewport={{ once: true, margin: "-100px" }} 
        transition={{ duration: 0.8, delay: 0.5, ease: "easeOut" }} 
      >
        <img src={leftphone} alt="left"  />
      </PhoneWrapper>

      {/* 오른쪽 휴대폰 */}
      <PhoneWrapper
        $zIndex={1}
        initial={{ x: 0, opacity: 0, rotate: 0 }}
        whileInView={{ x: 180, opacity: 1, rotate: 10 }}
        viewport={{ once: true, margin: "-100px" }}
        transition={{ duration: 0.8, delay: 0.5, ease: "easeOut" }}
      >
        <img src={rightphone} alt="right" />
      </PhoneWrapper>

      {/* 중앙 휴대폰 */}
      <PhoneWrapper
        $zIndex={2}
        initial={{ y: 10, opacity: 0 }}
        whileInView={{ y: -30, opacity: 1 }}
        viewport={{ once: true, margin: "-100px" }}
        transition={{ duration: 0.6, delay: 0.3 }}
      >
        <img src={centerphone} alt="center" style={{ width: '70%',  display: 'block', margin: '0 auto' }} />
      </PhoneWrapper>
    </AnimationContainer>
  );
};

export default PhoneAnimation;