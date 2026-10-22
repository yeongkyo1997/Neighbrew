import { callApi } from "./utils/api";
import { useState } from "react";
const Test = () => {
  const followHandler = async () => {
    await callApi("get", `api/push/follow/18`)
      .then(res => {
        console.log(res.data);
      })
      .catch(err => console.log(err));
  };
  const [, setMessages] = useState("");
  const inputHandler = e => {
    e.preventDefault();
    setMessages(e.target.value);
  };
  return (
    <div>
      <h1>Test</h1>
      <input type="text" onChange={inputHandler} />
      <button onClick={() => void followHandler()}>TESt</button>
    </div>
  );
};
export default Test;
