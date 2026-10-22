// 오류, 모임 메인 이동, 이미지 압축 중 모달
import { ModalInner } from "../../../style/common";
import { WhiteModal } from "../../../style/modalStyles";
import Modal from "react-modal";
import LoadingDot from "../../etc/LoadingDot";
import { MeetingInfoManageState } from "./useMeetingInfoManage";

const ManageModals = ({
  GoMeetMainHandler,
  isModalOn,
  setIsModalOn,
  isGotoMainModalOn,
  setIsGotoMainModalOn,
  errorMsg,
  loadingModalOn,
}: Pick<
  MeetingInfoManageState,
  | "GoMeetMainHandler"
  | "isModalOn"
  | "setIsModalOn"
  | "isGotoMainModalOn"
  | "setIsGotoMainModalOn"
  | "errorMsg"
  | "loadingModalOn"
>) => {
  return (
    <>
      <Modal isOpen={isModalOn} onRequestClose={() => setIsModalOn(false)} style={WhiteModal}>
        <ModalInner>{errorMsg}</ModalInner>
      </Modal>
      <Modal
        isOpen={isGotoMainModalOn}
        onRequestClose={() => {
          setIsGotoMainModalOn(false);
          GoMeetMainHandler(); //모임 메인 페이지로 이동
        }}
        style={WhiteModal}
      >
        <ModalInner>{errorMsg}</ModalInner>
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

export default ManageModals;
