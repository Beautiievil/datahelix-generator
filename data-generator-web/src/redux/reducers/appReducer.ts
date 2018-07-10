import {Action} from "redux";

import Actions from "../actions";
import {
	IAppState, ModalIds
} from "../state/IAppState";
import profileReducer from "./profileReducer";


export default function appReducer(
	oldState: IAppState | undefined,
	action: Action)
	: IAppState
{
	return {
		currentProfile: profileReducer(
			oldState ? oldState.currentProfile : undefined,
			action),

		currentModal: modalReducer(
			oldState ? oldState.currentModal : undefined,
			action)
	}
}

function modalReducer(
	oldState: ModalIds | undefined,
	action: Action)
	: ModalIds | undefined
{
	if (Actions.Modals.OpenModal.is(action)) {
		return action.modalId;
	}

	if (Actions.Modals.CloseModal.is(action)) {
		return undefined;
	}

	return oldState;
}