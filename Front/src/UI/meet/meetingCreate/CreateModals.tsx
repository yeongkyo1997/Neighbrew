// 오류 모달과 이미지 압축 중 모달
import { WhiteModal } from "../../../style/modalStyles";
import Modal from "react-modal";
import LoadingDot from "../../etc/LoadingDot";
import { MeetingCreateState } from "./useMeetingCreate";

const CreateModals = ({
  isModalOn,
  setIsModalOn,
  errorMsg,
  loadingModalOn,
}: Pick<
  MeetingCreateState,
  | "isModalOn"
  | "setIsModalOn"
  | "errorMsg"
  | "loadingModalOn"
>) => {
  return (
    <>
      <Modal isOpen={isModalOn} onRequestClose={() => setIsModalOn(false)} style={WhiteModal}>
        <div style={{ whiteSpace: "pre-line", overflow: "auto" }}>{errorMsg}</div>
      </Modal>
      <Modal
        isOpen={loadingModalOn}
        onRequestClose={() => undefined} //닫히지 않아야함
        style={WhiteModal}
      >
        <div style={{ whiteSpace: "pre-line", overflow: "auto", padding: "1rem" }}>
          <div style={{ paddingBottom: "0.5rem" }}>
            이미지 압축중입니다. <br /> 잠시만 기다려주세요.
          </div>
          <LoadingDot color="var(--c-yellow)" />
        </div>
      </Modal>
    </>
  );
};

export default CreateModals;
