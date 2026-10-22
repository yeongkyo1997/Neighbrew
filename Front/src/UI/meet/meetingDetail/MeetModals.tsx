// 나가기·삭제·승인거절 확인 모달, 모임 관리 모달, 오류 모달
import Modal from "react-modal";
import { ModalInner } from "../../../style/common";
import { WhiteModal } from "../../../style/modalStyles";
import { deleteIcon, editIcon } from "../../../assets/AllIcon";
import YesOrNoModal from "../../components/YesOrNoModal";
import { ManageModal, ModalIcon } from "./MeetingDetail.styles";
import { MeetingDetailState } from "./useMeetingDetail";

const MeetModals = ({
  meetId,
  exitModalOn,
  setExitModalOn,
  deleteModalOn,
  setDeleteModalOn,
  acceptModalOn,
  setAcceptModalOn,
  manageModalOn,
  setManageModalOn,
  simpleModalOn,
  setSimpleModalOn,
  targetUser,
  targetAction,
  errMsg,
  exitMeet,
  DeleteMeeting,
  memberHandler,
  GotoMeetInfoManage,
}: Pick<
  MeetingDetailState,
  | "meetId"
  | "exitModalOn"
  | "setExitModalOn"
  | "deleteModalOn"
  | "setDeleteModalOn"
  | "acceptModalOn"
  | "setAcceptModalOn"
  | "manageModalOn"
  | "setManageModalOn"
  | "simpleModalOn"
  | "setSimpleModalOn"
  | "targetUser"
  | "targetAction"
  | "errMsg"
  | "exitMeet"
  | "DeleteMeeting"
  | "memberHandler"
  | "GotoMeetInfoManage"
>) => {
  const DeleteIcon = deleteIcon();
  const EditIcon = editIcon();
  return (
    <>
      {/* 나가기모달 */}
      <Modal isOpen={exitModalOn} onRequestClose={() => setExitModalOn(false)} style={WhiteModal}>
        <YesOrNoModal
          msg="정말 이 모임에서 나가시겠습니까?"
          yesFunc={() => {
            exitMeet();
            setExitModalOn(false);
          }}
          noFunc={() => {
            setExitModalOn(false);
          }}
        />
      </Modal>
      {/* 모임 삭제 모달 */}
      <Modal
        isOpen={deleteModalOn}
        onRequestClose={() => setDeleteModalOn(false)}
        style={WhiteModal}
      >
        <YesOrNoModal
          msg="이 모임을 정말 삭제하시겠습니까?"
          yesFunc={() => {
            DeleteMeeting();
            setDeleteModalOn(false);
          }}
          noFunc={() => {
            setDeleteModalOn(false);
          }}
        />
      </Modal>
      {/* 승인거절 모달 */}
      <Modal
        isOpen={acceptModalOn}
        onRequestClose={() => setAcceptModalOn(false)}
        style={WhiteModal}
      >
        <YesOrNoModal
          msg={`유저 ${targetUser.nickname}을/를\n${targetAction ? "승인" : "거절"}하시겠습니까?`}
          yesFunc={() => {
            memberHandler(targetUser);
            setAcceptModalOn(false);
          }}
          noFunc={() => {
            setAcceptModalOn(false);
          }}
        />
      </Modal>
      {/* 모임 관리 모달 */}
      <Modal
        isOpen={manageModalOn}
        onRequestClose={() => setManageModalOn(false)}
        style={ManageModal}
      >
        <div
          style={{
            fontSize: "1rem",
            color: "var(--c-gray)",
          }}
        >
          모임 관리
        </div>
        <div
          onClick={() => GotoMeetInfoManage(parseInt(meetId))}
          style={{ display: "flex", alignItems: "center", height: "40%" }}
        >
          <ModalIcon>{EditIcon}</ModalIcon>
          <div
            style={{
              color: "black",
            }}
          >
            모임 정보 수정
          </div>
        </div>
        <div
          onClick={() => {
            setManageModalOn(false);
            setDeleteModalOn(true);
          }}
          style={{ display: "flex", alignItems: "center", height: "40%" }}
        >
          <ModalIcon>{DeleteIcon}</ModalIcon>
          <div style={{ color: "#eb0505" }}>삭제하기</div>
        </div>
      </Modal>
      {/* 에러 모달 */}
      <Modal
        isOpen={simpleModalOn}
        onRequestClose={() => setSimpleModalOn(false)}
        style={WhiteModal}
      >
        <ModalInner>{errMsg}</ModalInner>
      </Modal>
    </>
  );
};

export default MeetModals;
