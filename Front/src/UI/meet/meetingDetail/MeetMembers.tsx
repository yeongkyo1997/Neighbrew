// 참여 인원 목록과 방장에게만 보이는 참여 신청 목록
import PeopleNumInfo from "../PeopleNumInfo";
import UserInfoItem from "../../components/UserInfoItem";
import { SubTitle, OKBtn, NoBtn } from "./MeetingDetail.styles";
import { MeetingDetailState } from "./useMeetingDetail";

const MeetMembers = ({
  meetDetailData,
  memberList,
  applicantList,
  userStatus,
  setTargetAction,
  setTargetUser,
  setAcceptModalOn,
}: Pick<
  MeetingDetailState,
  | "meetDetailData"
  | "memberList"
  | "applicantList"
  | "userStatus"
  | "setTargetAction"
  | "setTargetUser"
  | "setAcceptModalOn"
>) => {
  return (
    <>
      <div style={{ display: "flex" }}>
        <SubTitle>참여 인원</SubTitle>
        <div
          style={{
            marginTop: "1.6rem",
            marginLeft: "0.5rem",
          }}
        >
          <PeopleNumInfo
            now={meetDetailData.meet.nowParticipants}
            max={meetDetailData.meet.maxParticipants}
            color="var(--c-black)"
            size={15}
          />
        </div>
      </div>
      <div style={{ margin: "0 0.5rem" }}>
        {memberList.map((member, index) => {
          return (
            <UserInfoItem key={member.userId} user={member} isMaster={index === 0} width={15} />
          );
        })}
      </div>
      {userStatus === "HOST" && (
        //신청자 목록
        <div>
          <SubTitle style={{ textAlign: "left", marginTop: "2rem" }}>참여 신청</SubTitle>
          <div style={{ margin: "0 1rem" }}>
            {applicantList.map(applicant => {
              return (
                <div
                  key={applicant.userId}
                  style={{
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "space-between",
                  }}
                >
                  <UserInfoItem user={applicant} isMaster={false} width={11} />
                  <div style={{ display: "flex" }}>
                    <OKBtn
                      onClick={() => {
                        setTargetAction(true);
                        setTargetUser(applicant);
                        setAcceptModalOn(true);
                      }}
                    >
                      <img src="/src/assets/checkButtonIcon.svg" />
                    </OKBtn>
                    <NoBtn
                      onClick={() => {
                        setTargetAction(false);
                        setTargetUser(applicant);
                        setAcceptModalOn(true);
                      }}
                    >
                      <img src="/src/assets/XbuttonIcon.svg" />
                    </NoBtn>
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      )}
    </>
  );
};

export default MeetMembers;
