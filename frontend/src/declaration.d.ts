declare module '*.module.css' {
    const classes: { [key: string]: string };
    export default classes;
}


declare module 'react-syntax-highlighter' {
    import { ComponentType } from 'react';

    export const Prism: any;
    export const SolarizedDark: any;
    export const SolarizedLight: any;
    export const Light: any;
    export const Dark: any;
}



export interface CodeProps {
    node: any;
    inline?: boolean;
    className?: string;
    children: React.ReactNode;
}

export interface MarkdownProps {
    children: string;
    components?: {
        code?: (props: CodeProps) => JSX.Element;
    };
}

declare module 'react-markdown' {
    const ReactMarkdown: React.FC<MarkdownProps>;
    export default ReactMarkdown;
}

declare module 'react-tooltip' {
    import * as React from 'react';

    interface TooltipProps {
        id: string;
        place?: 'top' | 'bottom' | 'left' | 'right';
        className?: string;
    }

    export const Tooltip: React.FC<TooltipProps>;
}
