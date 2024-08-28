import React from 'react';
import ReactMarkdown from 'react-markdown';
import { Prism as SyntaxHighlighter } from 'react-syntax-highlighter';
import { nightOwl } from 'react-syntax-highlighter/dist/esm/styles/prism';
import './MarkdownRenderer.css'; // Import custom styles

interface MarkdownRendererProps {
    markdownText: string;
}

const MarkdownRenderer: React.FC<MarkdownRendererProps> = ({ markdownText }) => {

    // Function to unescape escaped characters
    const unescapeMarkdown = (text: string) => {
        return text
            .replace(/\\n/g, '\n')  // Replaces \\n with newline
            .replace(/\\"/g, '"');  // Replaces \\" with "
    };

    const processedMarkdownText = unescapeMarkdown(markdownText);

    return (
        <ReactMarkdown
            children={processedMarkdownText}
            components={{
                code({ node, inline, className, children, ...props }: any) {
                    const language = className?.replace('language-', '') || 'text';
                    return !inline ? (
                        <SyntaxHighlighter
                            language={language}
                            style={nightOwl}
                            useInlineStyles={true}
                            className="code-block" // Apply custom class
                            {...props}
                        >
                            {String(children).replace(/\n$/, '')}
                        </SyntaxHighlighter>
                    ) : (
                        <code className={className} {...props}>
                            {children}
                        </code>
                    );
                },
            }}
        />
    );
};

export default MarkdownRenderer;
