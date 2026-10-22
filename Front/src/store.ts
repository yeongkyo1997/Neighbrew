import { createSlice, configureStore, PayloadAction } from "@reduxjs/toolkit";


const FooterMenu = createSlice({
  name: "chooseMenu",
  initialState: { value: 0 },
  reducers: {
    chooseMenu: (state, action: PayloadAction<number>) => {
      state.value = action.payload;
    },
  },
});

const store = configureStore({
  reducer: {
    chooseMenu: FooterMenu.reducer,
  },
});

export default store;

//state 타입을 export 해두는건데 나중에 쓸 데가 있음
export type RootState = ReturnType<typeof store.getState>;

//수정방법 만든거 export
export const { chooseMenu } = FooterMenu.actions;
