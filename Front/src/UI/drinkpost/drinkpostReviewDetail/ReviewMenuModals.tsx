// 수정·삭제 메뉴 시트와 삭제 확인 모달
import { deleteIcon, editIcon } from "../../../assets/AllIcon";
import Modal from "react-modal";
import { WhiteModal } from "../../../style/modalStyles";
import { ThreeDotModal, ModalBtnDiv, ModalBtn, ModalIcon } from "./DrinkpostReviewDetail.styles";
import { DrinkpostReviewDetailState } from "./useDrinkpostReviewDetail";

const ReviewMenuModals = ({
  deleteModalOn,
  setDeleteModalOn,
  threeDotOn,
  setThreeDotOn,
  toDeleteQuestionHandler,
  toUpdateHandler,
  deleteHandler,
}: Pick<
  DrinkpostReviewDetailState,
  | "deleteModalOn"
  | "setDeleteModalOn"
  | "threeDotOn"
  | "setThreeDotOn"
  | "toDeleteQuestionHandler"
  | "toUpdateHandler"
  | "deleteHandler"
>) => {
  const EditIcon = editIcon();
  const DeleteIcon = deleteIcon();
  return (
    <>
      <Modal
        isOpen={threeDotOn}
        onRequestClose={() => setThreeDotOn(false)}
        style={ThreeDotModal}
        ariaHideApp={false}
      >
        <div
          style={{
            fontSize: "1rem",
            color: "var(--c-gray)",
            fontFamily: "NanumSquareNeo",
          }}
        >
          후기
        </div>
        <div
          onClick={toUpdateHandler}
          style={{ display: "flex", alignItems: "center", height: "40%" }}
        >
          <ModalIcon>{EditIcon}</ModalIcon>
          <div
            style={{
              color: "black",
              fontSize: "16px",
              fontFamily: "NanumSquareNeo",
            }}
          >
            수정하기
          </div>
        </div>

        <div
          onClick={toDeleteQuestionHandler}
          style={{ display: "flex", alignItems: "center", height: "40%" }}
        >
          <ModalIcon>{DeleteIcon}</ModalIcon>
          <div
            style={{
              color: "#eb0505",
              fontSize: "16px",
              fontFamily: "NanumSquareNeo",
            }}
          >
            삭제하기
          </div>
        </div>
      </Modal>
      <Modal
        isOpen={deleteModalOn}
        onRequestClose={() => setDeleteModalOn(false)}
        style={WhiteModal}
        ariaHideApp={true}
      >
        <div>
          <div style={{ padding: "1rem 0 4rem 0" }}>정말 이 후기를 삭제하시겠습니까?</div>
          <ModalBtnDiv>
            <ModalBtn onClick={deleteHandler}>예</ModalBtn>
            <ModalBtn onClick={() => setDeleteModalOn(false)}>아니요</ModalBtn>
          </ModalBtnDiv>
        </div>
      </Modal>
    </>
  );
};

export default ReviewMenuModals;
